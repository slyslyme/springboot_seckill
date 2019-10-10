package com.sly.seckill.controller;

import com.sly.seckill.access.AccessLimit;
import com.sly.seckill.domain.OrderInfo;
import com.sly.seckill.domain.SeckillOrder;
import com.sly.seckill.domain.SeckillUser;
import com.sly.seckill.rabbitmq.MQSender;
import com.sly.seckill.rabbitmq.SeckillMessage;
import com.sly.seckill.redis.AccessKey;
import com.sly.seckill.redis.GoodsKey;
import com.sly.seckill.redis.RedisService;
import com.sly.seckill.redis.SeckillKey;
import com.sly.seckill.result.CodeMsg;
import com.sly.seckill.result.Result;
import com.sly.seckill.service.GoodsService;
import com.sly.seckill.service.OrderService;
import com.sly.seckill.service.SeckillService;
import com.sly.seckill.util.MD5Util;
import com.sly.seckill.util.UUIDUtil;
import com.sly.seckill.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.jws.WebParam;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/25 19:58
 * @package com.sly.seckill.controller
 **/
@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    @Autowired
    GoodsService goodsService;

    @Autowired
    SeckillService seckillService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender sender;

    private Map<Long, Boolean> localOverMap = new HashMap<>();

    /**
     * 系统初始化时调用该方法
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        if(goodsList == null)
            return;
        for(GoodsVo goods : goodsList){
            redisService.set(GoodsKey.seckillGoodsStock, ""+goods.getId(), goods.getStockCount());
            localOverMap.put(goods.getId(), false);
        }
    }

    @RequestMapping("/do_seckill")
    public String doSeckill(Model model, SeckillUser user, @RequestParam("goodsId")long goodsId){
        model.addAttribute("user", user);
        // 判断用户是否为空
        if(user == null)
            return "login";

        // 判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stockCount = goods.getStockCount();
        if(stockCount <= 0){
            model.addAttribute("errmsg", CodeMsg.MIAO_SHA_OVER.getMsg());
            return "seckill_fail";
        }

        // 判断是否秒杀到了
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null){
            model.addAttribute("errmsg", CodeMsg.REPEATE_MIAOSHA.getMsg());
            return "seckill_fail";
        }

        // 减库存 下订单 写入秒杀订单
        OrderInfo orderInfo = seckillService.seckill(user, goods);
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goods", goods);
        return "order_detail";
    }


    /**
     * c5: 秒杀逻辑（页面静态化分离，不需要直接将页面返回给客户端，而是返回客户端需要的页面动态数据，返回数据时json格式）
     * GET/POST的@RequestMapping是有区别的
     * 在c6之前，查询库存的时候，是直接去数据库中查询的
     * c6： 通过随机的path，客户端隐藏秒杀接口
     *
     * @param model
     * @param user
     * @param goodsId
     * //@param path    隐藏的秒杀地址，为客户端回传的path，最初也是有服务端产生的
     * @return 订单详情或错误码
     */
    // {path}为客户端回传的path，最初也是有服务端产生的
    @RequestMapping(value = "/{path}/do_seckill_static", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> doSeckillStatic(Model model, SeckillUser user,
                                           @RequestParam("goodsId") long goodsId,
                                           @PathVariable("path")String path) {

        model.addAttribute("user", user);
        // 判断用户是否为空
        if(user == null)
            return Result.error(CodeMsg.SESSION_ERROR);

        // 验证path
        boolean check = seckillService.checkPath(user, goodsId, path);
        if(!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

        // 内存标记，减少对redis的访问
        boolean over = localOverMap.get(goodsId);
        if(over){
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        // 预减库存
        Long stock = redisService.decr(GoodsKey.seckillGoodsStock, ""+goodsId);
        if(stock <= 0){
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        // 判断是否重复秒杀，取订单操作使用缓存
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null){
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }

        // 商品有库存且用户为秒杀商品，则将秒杀请求放入MQ
        SeckillMessage message = new SeckillMessage();
        message.setUser(user);
        message.setGoodsId(goodsId);

        // 放入MQ
        sender.sendSeckillMessage(message);
        return Result.success(0); // 排队中
    }


    /**
     * 用于返回用户秒杀结果
     * @param model
     * @param user
     * @param goodsId
     * @return orderId：成功, -1：秒杀失败, 0： 排队中
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> SeckillResult(Model model, SeckillUser user,
                                           @RequestParam("goodsId") long goodsId) {

        model.addAttribute("user", user);
        // 判断用户是否为空
        if (user == null)
            return Result.error(CodeMsg.SESSION_ERROR);
        long orderId = seckillService.getSeckillResult(user.getId(), goodsId);
        return Result.success(orderId);
    }


    /**
     * 获取秒杀接口地址
     * 每一次点击秒杀，都会生成一个随机的秒杀地址返回给客户端
     * 对秒杀的次数做限制（通过自定义拦截器注解实现）
     * @param user
     * @param goodsId
     * @return
     */
    @AccessLimit(seconds=10, maxCount=5, needLogin=true)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> SeckillPath(SeckillUser user, @RequestParam("goodsId") long goodsId, @RequestParam(value = "verifyCode", defaultValue = "0")int verifyCode) {

        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

       // 访问次数，使用的自定义拦截器，需要进行注册在WebConfig中
        /*String key = request.getRequestURI();
        key += "_"+user.getId();
        // 设置过期时间
        AccessKey accessKey = AccessKey.withExpire(5);
        // 在redis中存储的访问次数的key为请求的URI
        Integer count = redisService.get(accessKey, key, Integer.class);

        // 第一次重复点击秒杀
        if(count == null){
            redisService.set(accessKey, key, 1);
        } else if(count < 5){
            redisService.incr(accessKey, key);
        } else {
            // 点击次数已满
            return Result.error(CodeMsg.ACCESS_LIMIT_REACHED);
        }
*/

        // 验证验证码数据(点击更新验证码)（在获取秒杀路径之前，需要验证验证码）
        boolean check = seckillService.checkVerifyCode(user, goodsId, verifyCode);
        if(!check)
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        String path = seckillService.createSeckillPath(user, goodsId);
        return Result.success(path);
    }


    @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> SeckillVerifyCode(HttpServletResponse response, SeckillUser user, @RequestParam("goodsId") long goodsId) {
        if (user == null)
            return Result.error(CodeMsg.SESSION_ERROR);
        try {
            // 生成验证码（存入了redis）
            BufferedImage image = seckillService.createVerifyCode(user, goodsId);
            ServletOutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.close();
            out.flush();
            return null;
        } catch (Exception e){
            e.printStackTrace();
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }
    }

}

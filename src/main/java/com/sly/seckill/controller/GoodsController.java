package com.sly.seckill.controller;

import com.sly.seckill.domain.SeckillUser;
import com.sly.seckill.domain.User;
import com.sly.seckill.redis.GoodsKey;
import com.sly.seckill.redis.RedisService;
import com.sly.seckill.result.Result;
import com.sly.seckill.service.GoodsService;
import com.sly.seckill.service.SeckillUserService;
import com.sly.seckill.vo.GoodsDetailVo;
import com.sly.seckill.vo.GoodsVo;
import com.sly.seckill.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/24 20:44
 * @package com.sly.seckill.controller
 **/
@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    SeckillUserService seckillUserService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    RedisService redisService;

    // 因为在redis中不存在页面缓存时需要自己手动渲染，所以注入一个视图解析器，自定义渲染（默认是由springboot完成的）
    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    ApplicationContext applicationContext;

    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    /**
     * qps:230 5000 * 10
     * 使用页面级缓存，如果缓存中没有则需要手动渲染，并且将渲染的页面存储在redis中，供下一次访问时获取
     * 但是一般页面级缓存会设置缓存时间且时间不长，理由是保证页面发生变化时，不至于用户长时间看不到
     */
    @RequestMapping(value = "/to_list", produces = "text/html") // produces表示这个请求会返回text/HTML媒体类型的数据
    @ResponseBody
    public String toList(HttpServletRequest request, HttpServletResponse response, Model model, SeckillUser user){
        model.addAttribute("user", user);
        // 1. 从redis中取缓存
        String html = redisService.get(GoodsKey.goodsListKey, "", String.class);
        if(!StringUtils.isEmpty(html))
            return html;

        // 2. 如果redis中不存在该缓存则需要手动渲染
        // 查询商品列表，用于手动渲染时将商品数据填充至页面
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsList);

        // 3. 手动渲染HTML
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        // (第一个参数是渲染的HTML文件名，第二个是web上下文：里面封装了web应用的上下文)
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", webContext);

        if(!StringUtils.isEmpty(html)) // 如果HTML页面不为空则缓存页面
            redisService.set(GoodsKey.goodsListKey, "", html);

        return html;
    }


    /**
     * URL级缓存实现，从redis中取商品详情页面，如果没有则需要手动渲染页面，并且将渲染的页面存储在redis下，供下一次访问时获取
     * 实际上URL级缓存和页面缓存时一样的，只不过URL是根据url的参数从redis中取不同的数据
     */
    @RequestMapping(value = "/to_detail/{goodsId}", produces = "text/html")
    @ResponseBody
    public String toDetail(HttpServletRequest request, HttpServletResponse response,
            Model model, SeckillUser user, @PathVariable("goodsId")long goodsId) {

        // 1. 根据商品id从redis中取详情数据的缓存
        String html = redisService.get(GoodsKey.goodsDetailKey, ""+goodsId, String.class);
        if(!StringUtils.isEmpty(html)) // 如果缓存中存在数据就直接返回
            return html;
        // 2. 如果缓存中数据不存在，则需要手动渲染详情界面数据并返回
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goods);

        // 获取秒杀商品的开始时间和结束时间
        long startTime = goods.getStartDate().getTime(); // ms
        long endTime = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        // 秒杀状态 0：未开始  1：进行中哦  2：已结束
        int seckillStatus = 0;
        // 秒杀倒计时时间
        int remainSeconds = 0;
        if(now < startTime){
            seckillStatus = 0;
            remainSeconds = (int)((startTime-now) / 1000);
        } else if(now > endTime){
            seckillStatus = 2;
            remainSeconds = -1;
        } else {
            seckillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("seckillStatus", seckillStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        model.addAttribute("user", user);
        // return "goods_detail"; // 修改为URL缓存

        // 3. 渲染HTML
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", webContext);
        if(!StringUtils.isEmpty(html)) // HTML不空，则将页面缓存在redis中
            redisService.set(GoodsKey.goodsDetailKey, ""+goodsId, html);
        return html;
    }


    /**
     * 页面静态化处理商品详情页，直接将数据返回给客户端，交给客户端处理
     */
    @RequestMapping(value = "/to_detail_static/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> toDetail_static(HttpServletRequest request, HttpServletResponse response,
                                                    Model model, SeckillUser user, @PathVariable("goodsId")long goodsId) {

        // 通过id在数据库中查询
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);

        // 获取秒杀商品的开始时间和结束时间
        long startTime = goods.getStartDate().getTime(); // ms
        long endTime = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        // 秒杀状态 0：未开始  1：进行中哦  2：已结束
        int seckillStatus = 0;
        // 秒杀倒计时时间
        int remainSeconds = 0;
        if(now < startTime){
            seckillStatus = 0;
            remainSeconds = (int)((startTime-now) / 1000);
        } else if(now > endTime){
            seckillStatus = 2;
            remainSeconds = -1;
        } else {
            seckillStatus = 1;
            remainSeconds = 0;
        }

        GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
        goodsDetailVo.setGoodsVo(goods);
        goodsDetailVo.setRemainSeconds(remainSeconds);
        goodsDetailVo.setSeckillStatus(seckillStatus);
        goodsDetailVo.setUser(user);

        return Result.success(goodsDetailVo);
    }


}

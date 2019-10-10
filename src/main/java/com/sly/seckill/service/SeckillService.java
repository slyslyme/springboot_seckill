package com.sly.seckill.service;

import com.sly.seckill.domain.Goods;
import com.sly.seckill.domain.OrderInfo;
import com.sly.seckill.domain.SeckillOrder;
import com.sly.seckill.domain.SeckillUser;
import com.sly.seckill.redis.RedisService;
import com.sly.seckill.redis.SeckillKey;
import com.sly.seckill.util.MD5Util;
import com.sly.seckill.util.UUIDUtil;
import com.sly.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/25 20:09
 * @package com.sly.seckill.service
 **/
@Service
public class SeckillService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    @Transactional
    public OrderInfo seckill(SeckillUser user, GoodsVo goods) {
        // 减库存是否成功
        boolean success = goodsService.reduceStock(goods);
        if(!success){
            setGoodsOver(goods.getId());
            return null;
        }
        // 订单写入：order_info seckill_order
        return orderService.createOrder(user, goods);
    }


    /**
     * 获取秒杀结果
     * @param userId
     * @param goodsId
     * @return
     */
    public long getSeckillResult(Long userId, long goodsId) {
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(userId, goodsId);
        if(order != null)
            return order.getOrderId(); // 成功
        else {
            boolean isOver = getGoodsOver(goodsId);
            if(isOver)
                return -1;  // 失败
            else
                return 0;  // 排队中
        }
    }

    private void setGoodsOver(Long goodsId) {
        // 在缓存中设置已经卖完
        redisService.set(SeckillKey.isGoodsOver, ""+goodsId, true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(SeckillKey.isGoodsOver, ""+goodsId);
    }

    /**
     *
     * @param user
     * @param goodsId
     * @param path
     * @return
     */
    public boolean checkPath(SeckillUser user, long goodsId, String path) {
        if(user == null || path == null)
            return false;
        // 在缓存中是否存在
        String pathOld  = redisService.get(SeckillKey.seckillPath, ""+user.getId()+"_"+goodsId, String.class);
        return path.equals(pathOld);
    }

    public String createSeckillPath(SeckillUser user, long goodsId) {
        if(user == null || goodsId <= 0)
            return null;
        // 生成随机串
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        // 生成后存入缓存
        redisService.set(SeckillKey.seckillPath, ""+user.getId()+"_"+goodsId, str);
        return str;
    }

    public BufferedImage createVerifyCode(SeckillUser user, long goodsId) {
        if(user == null || goodsId <= 0)
            return null;
        // 验证码的宽高
        int width = 80;
        int height = 32;

        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion 干扰
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();

        // 计算表达式值，并把把验证码值存到redis中
        int expResult = calc(verifyCode);
        redisService.set(SeckillKey.seckillVerifyCode, user.getId() + "," + goodsId, expResult);
        //输出图片
        return image;
    }

    /**
     * 生成运算式
     * + - * 运算
     * @param rdm
     * @return
     */
    private static char[] ops = new char[]{'+', '-', '*'}; // 运算
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = "" + num1 + op1 + num2 + op2 + num3;
        return exp;
    }

    /**
     * 计算表达式，使用scriptEngine，验证过，有运算符优先级的考虑
     * @param exp
     * @return
     */
    private int calc(String exp) {
        try{
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript"); // 拿到引擎
            return (Integer)engine.eval(exp);
        } catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public boolean checkVerifyCode(SeckillUser user, long goodsId, int verifyCode) {
        if(user == null || goodsId <= 0)
            return false;
        Integer codeOld = redisService.get(SeckillKey.seckillVerifyCode, ""+user.getId()+","+goodsId, Integer.class);
        if(codeOld == null || codeOld - verifyCode != 0)
            return false;
        // 删去redis中存放的该验证码,防止用户再次使用
        redisService.delete(SeckillKey.seckillVerifyCode, ""+user.getId()+","+goodsId);
        return true;
    }
}

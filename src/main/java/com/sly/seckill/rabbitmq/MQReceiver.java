package com.sly.seckill.rabbitmq;

import com.sly.seckill.domain.SeckillOrder;
import com.sly.seckill.domain.SeckillUser;
import com.sly.seckill.redis.RedisService;
import com.sly.seckill.service.GoodsService;
import com.sly.seckill.service.OrderService;
import com.sly.seckill.service.SeckillService;
import com.sly.seckill.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/30 15:12
 * @package com.sly.seckill.rabbitmq
 **/
@Service
public class MQReceiver {

    private static Logger logger = LoggerFactory.getLogger(MQReceiver.class);

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    SeckillService seckillService;


    /**
     * 处理收到的秒杀成功消息
     * @param message
     */
    @RabbitListener(queues = {MQConfig.SECKILL_QUEUE})
    public void receiveSeckillInfo(String message){
        logger.info("Seckill MQ receive message :" + message);
        SeckillMessage seckillMessage = RedisService.stringToBean(message, SeckillMessage.class);

        // 获取秒杀用户信息和商品ID
        SeckillUser user = seckillMessage.getUser();
        long goodsId = seckillMessage.getGoodsId();

        // 获取商品库存,从数据库中获取,因为在SeckillController使用decr进行了库存预减，所以到此处的请求已经没有那么多
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        Integer stockCount = goods.getStockCount();
        if(stockCount <= 0)
            return;

        // 判断是否秒杀到了，从缓存中查看订单
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null)
            return;

        // 减库存 下订单 写入秒杀订单
        seckillService.seckill(user, goods);
    }



    /**
     * 下面四种为四种交换机模式的接收练习代码
     * @param message
     */
    @RabbitListener(queues = {MQConfig.QUEUE})
    public void receive(String message){
        logger.info("MQ : message :" + message);
    }

    @RabbitListener(queues = {MQConfig.TOPIC_QUEUE1})
    public void receiveTopic1(String message){
        logger.info("MQ1 : message :" + message);
    }

    @RabbitListener(queues = {MQConfig.TOPIC_QUEUE2})
    public void receiveTopic2(String message){
        logger.info("MQ2 : message :" + message);
    }

    @RabbitListener(queues =MQConfig.HEADER_QUEUE)
    public void receiveHeader(byte[] message){
        logger.info("Header receive message :" + new String(message));
    }
}

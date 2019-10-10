package com.sly.seckill.rabbitmq;

import com.sly.seckill.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/30 15:11
 * @package com.sly.seckill.rabbitmq
 **/
@Service
public class MQSender {

    private static Logger logger = LoggerFactory.getLogger(MQSender.class);

    @Autowired
    AmqpTemplate amqpTemplate;

    public void sendSeckillMessage(SeckillMessage message){
        String msg = RedisService.beanToString(message);
        logger.info("MQ send message:" + msg);
        amqpTemplate.convertAndSend(MQConfig.SECKILL_QUEUE, msg);
    }




    /**
     * 以下为RabbitMQ的四种模式
     * @param message
     */
    public void sendDirect(Object message){
        String msg = RedisService.beanToString(message);
        logger.info("MQ send message : " + msg);
        // 第一个参数为消息队列名，第二个参数是发送的消息
        amqpTemplate.convertAndSend(MQConfig.QUEUE, msg);
    }

    public void sendTopic(Object message){
        String msg = RedisService.beanToString(message);
        logger.info("send topic message : " + msg);
        // 将消息投递到topic exchange
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key1", msg + "1");
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key2", msg + "2");
    }

    public void sendFanout(Object message){
        String msg = RedisService.beanToString(message);
        logger.info("send fanout message : " + msg);
        // Fanout Exchange 消息广播的模式，不管路由键或者是路由模式，会把消息发给绑定给它的全部队列，如果配置了 routing_key 会被忽略。
        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE, "", msg);
    }

    public void sendHeader(Object message){
        String msg = RedisService.beanToString(message);
        logger.info("send header message : " + msg);
        MessageProperties properties = new MessageProperties();
        properties.setHeader("header1", "value1");
        properties.setHeader("header2", "value2");
        Message obj = new Message(msg.getBytes(), properties);
        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE, "", obj);
    }
}

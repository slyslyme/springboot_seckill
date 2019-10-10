package com.sly.seckill.rabbitmq;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/30 15:12
 * @package com.sly.seckill.rabbitmq
 **/
@Configuration
public class MQConfig {
    // 消息队列名
    public static final String SECKILL_QUEUE = "seckill.queue";
    public static final String QUEUE = "queue";
    public static final String TOPIC_QUEUE1 = "topic.queue1";
    public static final String TOPIC_QUEUE2 = "topic.queue2";
    public static final String HEADER_QUEUE = "header.queue";
    public static final String TOPIC_EXCHANGE = "topicExchange";
    public static final String FANOUT_EXCHANGE = "fanoutExchange";
    public static final String HEADERS_EXCHANGE = "headersExchange";


    /**
     * 秒杀Queue
     * @return
     */
    @Bean
    public Queue seckillQueue(){
        return new Queue(SECKILL_QUEUE, true);
    }

    /**
     * Direct模式 交换机exchange
     * @return 消息队列
     */
    @Bean
    public Queue queue(){
        return new Queue(QUEUE, true);
    }

    /**
     * Topic模式 交换机exchange
     * @return
     */
    @Bean
    public Queue topicQueue1(){
        return new Queue(TOPIC_QUEUE1, true);
    }

    @Bean
    public Queue topicQueue2(){
        return new Queue(TOPIC_QUEUE2, true);
    }
    /**
     * 创建一个topic exchange
     */
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    @Bean
    public Binding topicBinding1(){
        // 将topicQueue1绑定到topicExchange，接收routingKey为topic.key1的消息
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with("topic.key1");
    }

    @Bean
    public Binding topicBinding2(){
        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with("topic.#");
    }

    /**
     * 创建一个fanout exchange
     */
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    @Bean
    public Binding fanoutBinding1(){
        // 将topicQueue1绑定到topicExchange，接收routingKey为topic.key1的消息
        return BindingBuilder.bind(topicQueue1()).to(fanoutExchange());
    }

    @Bean
    public Binding fanoutBinding2(){
        return BindingBuilder.bind(topicQueue2()).to(fanoutExchange());
    }


    /**
     * header模式 交换机exchange
     * @return
     */
    @Bean
    public HeadersExchange headersExchange(){
        return new HeadersExchange(HEADERS_EXCHANGE);
    }

    @Bean
    public Queue headerQueue(){
        return new Queue(HEADER_QUEUE, true);
    }

    @Bean
    public Binding headerBinding(){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("header1", "value1");
        map.put("header2", "value2");
        return BindingBuilder.bind(headerQueue()).to(headersExchange()).whereAll(map).match();
    }
}

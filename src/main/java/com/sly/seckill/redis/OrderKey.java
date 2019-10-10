package com.sly.seckill.redis;

import com.sun.xml.internal.rngom.parse.host.Base;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/23 21:20
 * @package com.sly.seckill.redis
 **/

/**
 * 存储订单的key前缀
 */
public class OrderKey extends BasePrefix {

    private OrderKey(int expireSeconds, String prefix){
        super(expireSeconds, prefix);
    }

    public static OrderKey getSeckillOrderByUidGid = new OrderKey(0,"OrderUidGid");
}

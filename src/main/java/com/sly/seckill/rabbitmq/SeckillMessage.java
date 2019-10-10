package com.sly.seckill.rabbitmq;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/31 9:54
 * @package com.sly.seckill.rabbitmq
 **/

import com.sly.seckill.domain.SeckillUser;

/**
 * 在MQ中传递秒杀信息
 * 包含参加秒杀的用户和商品ID
 */
public class SeckillMessage {
    private SeckillUser user;
    private long goodsId;

    public SeckillUser getUser() {
        return user;
    }

    public void setUser(SeckillUser user) {
        this.user = user;
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }

    @Override
    public String toString() {
        return "SeckillMessage{" +
                "user=" + user +
                ", goodsId=" + goodsId +
                '}';
    }
}

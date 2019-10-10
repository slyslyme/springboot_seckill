package com.sly.seckill.vo;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/25 12:00
 * @package com.sly.seckill.vo
 **/

import com.sly.seckill.domain.SeckillUser;

/**
 * 商品详情
 * 用于将数据传递给客户端
 */
public class GoodsDetailVo {
    private int seckillStatus = 0;
    private int remainSeconds = 0;
    private GoodsVo goodsVo;
    private SeckillUser user;

    public int getSeckillStatus() {
        return seckillStatus;
    }

    public void setSeckillStatus(int seckillStatus) {
        this.seckillStatus = seckillStatus;
    }

    public int getRemainSeconds() {
        return remainSeconds;
    }

    public void setRemainSeconds(int remainSeconds) {
        this.remainSeconds = remainSeconds;
    }

    public GoodsVo getGoodsVo() {
        return goodsVo;
    }

    public void setGoodsVo(GoodsVo goodsVo) {
        this.goodsVo = goodsVo;
    }

    public SeckillUser getUser() {
        return user;
    }

    public void setUser(SeckillUser user) {
        this.user = user;
    }
}

package com.sly.seckill.vo;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/25 12:01
 * @package com.sly.seckill.vo
 **/

import com.sly.seckill.domain.OrderInfo;

/**
 * 订单详情，包含订单信息和商品信息
 * 用于将数据传递给客户端
 */
public class OrderDetailVo {
    private GoodsVo goods;// 商品信息
    private OrderInfo order; // 订单信息

    public GoodsVo getGoods() {
        return goods;
    }

    public void setGoods(GoodsVo goods) {
        this.goods = goods;
    }

    public OrderInfo getOrder() {
        return order;
    }

    public void setOrder(OrderInfo order) {
        this.order = order;
    }
}

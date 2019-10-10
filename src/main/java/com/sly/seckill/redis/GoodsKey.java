package com.sly.seckill.redis;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/28 16:04
 * @package com.sly.seckill.redis
 **/

import com.sly.seckill.domain.Goods;

/**
 * redis中，用于商品信息的key
 */
public class GoodsKey extends BasePrefix{

    public GoodsKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    // 缓存在redis中商品列表页面的key前缀
    public static GoodsKey goodsListKey = new GoodsKey(60, "gl");

    // 缓存在redis中商品详情页的key前缀
    public static GoodsKey goodsDetailKey = new GoodsKey(60, "gd");

    // 缓存在redis中商品库存的前缀
    public static GoodsKey seckillGoodsStock = new GoodsKey(0, "goodsStock");
}

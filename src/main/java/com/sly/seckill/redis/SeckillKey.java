package com.sly.seckill.redis;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/31 11:07
 * @package com.sly.seckill.redis
 **/
public class SeckillKey extends BasePrefix{

    public SeckillKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static SeckillKey isGoodsOver = new SeckillKey(0,"isGoodsOver");
    public static SeckillKey seckillPath = new SeckillKey(60,"seckillPath");
    public static SeckillKey seckillVerifyCode = new SeckillKey(300,"seckillVerifyCode"); // 有效时间5分钟
}

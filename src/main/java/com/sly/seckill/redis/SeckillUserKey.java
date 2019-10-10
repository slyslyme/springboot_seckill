package com.sly.seckill.redis;

import com.sly.seckill.domain.SeckillUser;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/24 20:31
 * @package com.sly.seckill.redis
 **/
public class SeckillUserKey extends BasePrefix{
    // 设置过期时间2天
    public static final int TOKEN_EXPIRE = 3600 * 24 * 2;

    private SeckillUserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static SeckillUserKey token = new SeckillUserKey(TOKEN_EXPIRE, "tk");
    public static SeckillUserKey getById = new SeckillUserKey(0, "id"); // 对象缓存设置永久有效
}

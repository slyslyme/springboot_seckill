package com.sly.seckill.redis;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/9/1 7:22
 * @package com.sly.seckill.redis
 **/

/**
 * 访问次数的key前缀
 */
public class AccessKey extends BasePrefix{

    private AccessKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    // 可灵活设置过期时间
    public static AccessKey withExpire(int expireSeconds) {
        return new AccessKey(expireSeconds, "access");
    }
}

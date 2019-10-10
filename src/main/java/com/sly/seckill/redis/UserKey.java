package com.sly.seckill.redis;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/23 21:19
 * @package com.sly.seckill.redis
 **/
public class UserKey extends BasePrefix {
    private UserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static UserKey getById = new UserKey(0,"id");
    public static UserKey getGetByName = new UserKey(0,"name");
}

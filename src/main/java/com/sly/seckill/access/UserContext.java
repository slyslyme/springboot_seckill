package com.sly.seckill.access;

import com.sly.seckill.domain.SeckillUser;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/9/1 7:58
 * @package com.sly.seckill.access
 **/

/**
 * 用于保存用户
 * 使用ThreadLocal保存用户，因为ThreadLocal是线程安全的，使用ThreadLocal可以保存当前线程持有的对象
 * 每个用户的请求对应一个线程，所以使用ThreadLocal以线程为键保存用户是合适的
 */
public class UserContext {

    // 保存用户的容器
    private static ThreadLocal<SeckillUser> userHolder = new ThreadLocal<>();

    public static void setUser(SeckillUser user) {
        userHolder.set(user);
    }

    public static SeckillUser getUser() {
        SeckillUser user = userHolder.get();
        if(user != null)
        System.out.println(user.getId());
        return user;
    }
}

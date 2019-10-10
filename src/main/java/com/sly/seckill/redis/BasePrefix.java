package com.sly.seckill.redis;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/23 21:08
 * @package com.sly.seckill.redis
 **/

/**
 * 功能：对不同模块添加不同的redis键前缀，防止键被覆盖
 * 模板方法的基本类
 */
public abstract class BasePrefix implements KeyPrefix{

    private int expireSeconds; // 过期时间
    private String prefix;   // 前缀

    /**
     * 默认过期时间为0，即不过期，过期时间只收到redis缓存策略的影响
     * @param prefix  前缀
     */
    public BasePrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    /**
     * 默认0代表永不过期
     * @return
     */
    @Override
    public int expireSeconds() {
        return expireSeconds;
    }

    /**
     * 前缀为模板类的实现类类名
     * @return
     */
    @Override
    public String getPrefix() {
        String className = getClass().getSimpleName();
        return className + ":" + prefix;
    }
}

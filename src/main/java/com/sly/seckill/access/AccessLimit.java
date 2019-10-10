package com.sly.seckill.access;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/9/1 7:58
 * @package com.sly.seckill.access
 **/

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 用户访问拦截的注解，主要用于防刷功能
 */
@Retention(RetentionPolicy.RUNTIME)//运行期间有效
@Target(ElementType.METHOD)//注解类型为方法注解
public @interface AccessLimit {

    // 最大请求次数的间隔
    int seconds();

    // 最大请求次数
    int maxCount();

    // 是否需要登录
    boolean needLogin() default true;

}

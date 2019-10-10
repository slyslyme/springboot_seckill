package com.sly.seckill.access;

import com.alibaba.fastjson.JSON;
import com.sly.seckill.domain.SeckillUser;
import com.sly.seckill.exception.GlobalException;
import com.sly.seckill.redis.AccessKey;
import com.sly.seckill.redis.RedisService;
import com.sly.seckill.result.CodeMsg;
import com.sly.seckill.result.Result;
import com.sly.seckill.service.SeckillUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.HandlerMethod;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/9/1 7:58
 * @package com.sly.seckill.access
 **/

@Service
public class AccessInterceptor implements HandlerInterceptor {

    @Autowired
    SeckillUserService seckillUserService;

    @Autowired
    RedisService redisService;

    private static Logger logger = LoggerFactory.getLogger(AccessInterceptor.class);

    /**
     * 目标方法执行前的处理
     * 查询访问次数，进行防刷请求拦截
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        try {
            // 指明拦截的是方法
            /*if (!handler.getClass().isAssignableFrom(HandlerMethod.class)) {
                System.out.println("kkkkk2");
                return true;
            }*/
            if (handler instanceof HandlerMethod) {
                // 获取用户对象
                SeckillUser user = getUser(request, response);
                // 保存用户到ThreadLocal,这样，同一线程访问的是同一用户
                UserContext.setUser(user);
                HandlerMethod hm = (HandlerMethod) handler;
                // 获取注解了@AccessLimit的方法，没有注解则直接返回
                AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
                if (accessLimit == null)
                    return true;

                // 获取注解的元素值
                int seconds = accessLimit.seconds();
                int maxCount = accessLimit.maxCount();
                boolean needLogin = accessLimit.needLogin();

                String key = request.getRequestURI();
                if (needLogin) {
                    if (user == null) {
                        this.render(response, CodeMsg.SESSION_ERROR);
                        return false;
                    }
                    key += "_" + user.getId();
                } else {
                    // do nothing
                }

                // 设置过期时间
                AccessKey accessKey = AccessKey.withExpire(seconds);
                // 在redis中存储的访问次数的key为请求的URI
                Integer count = redisService.get(accessKey, key, Integer.class);

                // 第一次重复点击秒杀
                if (count == null) {
                    redisService.set(accessKey, key, 1);
                } else if (count < maxCount) {
                    redisService.incr(accessKey, key);
                } else {
                    // 点击次数已满
                    this.render(response, CodeMsg.ACCESS_LIMIT_REACHED);
                    return false;
                }
            }
        } catch (Exception e){
            logger.error("API请求限流拦截异常，异常原因：", e);
            e.printStackTrace();
        }
        return true;
    }


    /**
     * 点击次数已满后，向客户端反馈一个“频繁请求”提示信息
     * @param response
     * @param cm
     */
    private void render(HttpServletResponse response, CodeMsg cm) throws IOException {
        response.setContentType("application/json;charset=UTF-8"); // 指定编码方式
        OutputStream out = response.getOutputStream();
        String str = JSON.toJSONString(Result.error(cm));
        out.write(str.getBytes("UTF-8"));
        out.flush();
        out.close();
    }

    //根据token获取用户信息
    //注意：若前端直接传送token过来则使用该token，否则使用cookie中的token
    private SeckillUser getUser(HttpServletRequest request, HttpServletResponse response) {

        String paramToken = request.getParameter(SeckillUserService.COOKIE_NAME_TOKEN);
        String cookieToken = getCookieValue(request, SeckillUserService.COOKIE_NAME_TOKEN);
        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return null;
        }
        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
        System.out.println("token : " + token);
        return seckillUserService.getByToken(response,token);
    }


    //从cookie中获取token
    private String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length <= 0) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * 和UserArgumentResolver功能类似，用于解析拦截的请求的，获取seckillUser对象
     * @param request
     * @param response
     * @return
     *//*
    private SeckillUser getUser(HttpServletRequest request, HttpServletResponse response) {
        // 从请求对象中获取token（token可能有两种方式从客户端返回，1：通过url的参数， 2：通过set-cookie字段）
        String paramToken = request.getParameter(SeckillUserService.COOKIE_NAME_TOKEN);
        String cookieToken = getCookieValue(request, SeckillUserService.COOKIE_NAME_TOKEN);
        if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)){
            return null;
        }
        // 判断是哪种方式返回的token，并由该种方式获取token(cookie)
        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
        // 通过token就可以在redis中查出token对应的用户对象
        return seckillUserService.getByToken(response, token);
    }

    *//**
     * 从众多cookie中找到指定cookieName的cookie
     * @param request
     * @param cookieNameToken
     * @return
     *//*
    private String getCookieValue(HttpServletRequest request, String cookieNameToken) {
        Cookie[] cookies = request.getCookies();
        // null判断，否则并发时会出现异常
        if (cookies == null || cookies.length <= 0) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieNameToken)) {
                return cookie.getValue();
            }
        }
        return null;
    }*/
}

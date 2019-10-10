package com.sly.seckill.service;

import com.sly.seckill.dao.SeckillUserDao;
import com.sly.seckill.domain.SeckillUser;
import com.sly.seckill.exception.GlobalException;
import com.sly.seckill.redis.RedisService;
import com.sly.seckill.redis.SeckillUserKey;
import com.sly.seckill.result.CodeMsg;
import com.sly.seckill.util.MD5Util;
import com.sly.seckill.util.UUIDUtil;
import com.sly.seckill.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.net.CookieHandler;
import java.util.UUID;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/24 14:44
 * @package com.sly.seckill.service
 **/
@Service
public class SeckillUserService {
    @Autowired
    SeckillUserDao seckillUserDao;

    // 由于需要将一个cookie对应的用户存入第三方缓存中，这里用redis，所以需要引入redis serice
    @Autowired
    RedisService redisService;

    public static final String COOKIE_NAME_TOKEN = "token";

    // 对象缓存改造
    public SeckillUser getById(Long id){
        // 取缓存，设置的有效期是永久有效
        SeckillUser user = redisService.get(SeckillUserKey.getById, "" + id, SeckillUser.class);
        if(user != null)
            return user;
        // 缓存中没有则直接查找数据库,并将最终查到的数据添加到redis中
        user = seckillUserDao.getById(id);
        if(user != null)
            redisService.set(SeckillUserKey.getById, "" + id, user);
        return user;
    }

    /**
     * 用户登录, 要么处理成功返回true，否则会抛出全局异常
     * 抛出的异常信息会被全局异常接收，全局异常会将异常信息传递到全局异常处理器
     * @param response
     * @param loginVo  封装了客户端请求传递过来的数据（即账号密码）
     * @return
     */
    public String login(HttpServletResponse response, LoginVo loginVo) {
        if(loginVo == null)
            // 抛出的异常信息会被全局异常接收，全局异常会将异常信息传递到全局异常处理器
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        // 判断手机号是否存在
        SeckillUser user = getById(Long.parseLong(mobile));
        if(user == null)
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST); // 不存在手机号
        // 验证密码
        String dbPass = user.getPassword();
        String slatDB = user.getSalt();
        String calcPass = MD5Util.serverPassToDBPass(formPass, slatDB);
        if(!calcPass.equals(dbPass))
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        //生成cookie,只有在重新登录时才设置token,否则只更新时间
        String token = UUIDUtil.uuid();
        addCookie(response, token, user);
        return token;
    }

    /**
     * 将cookie写入redis中，并将cookie写入到请求的响应中
     * @param response
     * @param user
     */
    private void addCookie(HttpServletResponse response, String token, SeckillUser user){
        // 核心就是将私人信息存放入第三方缓存中
        redisService.set(SeckillUserKey.token, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(SeckillUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * 根据token从redis中取出SeckillUser对象
     * @param response 获取对象的同时，将新的cookie设置到response中
     * @param token    用于在redis中获取SeckillUser对象的key
     * @return
     */
    public SeckillUser getByToken(HttpServletResponse response, String token) {
        if(StringUtils.isEmpty(token))
            return null;

        SeckillUser user = redisService.get(SeckillUserKey.token, token, SeckillUser.class);
        // 延长有效期,重新向缓存中设置值
        if(user != null)
            addCookie(response, token, user);
        return user;
    }

    /**
     * 更新用户密码
     * @param token
     * @param id
     * @param password
     * @return
     */
    public boolean updatePassword(String token, long id, String password){
        // 取user,判断是否存在
        SeckillUser user = getById(id); // 这一步也可能是从缓存中读取数据
        if(user == null)
            throw  new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        // 如果用户存在，则更新数据库
        SeckillUser toBeUpdate = new SeckillUser();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.serverPassToDBPass(password, user.getSalt()));
        // 注意不可以先让缓存失效，因为这样的话可能会造成数据不一致
        seckillUserDao.updatePassword(toBeUpdate);
        // 处理缓存: token getById，一定注意更新缓存，否则会造成数据不一致
        redisService.delete(SeckillUserKey.getById, "" + id);
        // 因为token删除后将导致无法登陆，所以应当更新而非删除
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(SeckillUserKey.token, token, user);
        return true;
    }
}

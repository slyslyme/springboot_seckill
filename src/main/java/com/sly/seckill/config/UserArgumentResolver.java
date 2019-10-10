package com.sly.seckill.config;

import com.sly.seckill.access.UserContext;
import com.sly.seckill.domain.SeckillUser;
import com.sly.seckill.service.SeckillUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/24 23:18
 * @package com.sly.seckill.config
 **/

/**
 * 解析请求，并将请求的参数设置到方法参数中
 */
@Service // 使用spring管理起来
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    SeckillUserService seckillUserService;

    /**
     * 当请求参数为SeckillUser时，使用这个解析器处理
     * 客户端的请求到达某个Controller方法时，判断这个方法的参数是否是SeckillUser
     * 如果是，则这个SeckillUser参数通过下面的resolveArgument()方法获取
     * 然后，该Controller方法继续往下执行时看到的SeckillUser对象就是这里resolveArgument处理过的对象
     * @param methodParameter
     * @return
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        // 获取参数类型,判断是否是SeckillUser类型，是则执行下面的resolveArgument
        Class<?> clazz = methodParameter.getParameterType();
        return clazz == SeckillUser.class;
    }

    /**
     * 获取SeckillUser对象
     * @param methodParameter
     * @param modelAndViewContainer
     * @param nativeWebRequest
     * @param webDataBinderFactory
     * @return
     * @throws Exception
     */
    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {

        /*System.out.println("解析：" + UserContext.getUser().getId());*/
        System.out.println("解析参数user");
        /*return UserContext.getUser();*/

        // 获取请求和响应对象
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);

        //return UserContext.getUser();

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

    /**
     * 根据cookie名获取相应的cookie值
     * @param request
     * @param cookieName
     * @return
     */
    private String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        // null判断，否则并发时会出现异常
        if(cookies == null || cookies.length <= 0)
            return null;
        for(Cookie cookie: cookies){
            if(cookie.getName().equals(cookieName)){
                return cookie.getValue();
            }
        }
        return null;
    }
}

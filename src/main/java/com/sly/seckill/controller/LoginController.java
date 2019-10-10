package com.sly.seckill.controller;

import com.sly.seckill.result.CodeMsg;
import com.sly.seckill.result.Result;
import com.sly.seckill.service.SeckillUserService;
import com.sly.seckill.util.ValidatorUtil;
import com.sly.seckill.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.management.relation.RelationSupport;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/24 10:09
 * @package com.sly.seckill.controller
 **/
@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    SeckillUserService seckillUserService;

    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping("/to_login")
    public String toLogin(){
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<Boolean> doLogin(HttpServletResponse response, @Valid LoginVo loginVo){
        log.info(loginVo.toString());
        seckillUserService.login(response, loginVo);
        return Result.success(true);
    }

    /**
     * 这个请求处理用于生成token，压测时候用
     *
     * @param response
     * @param loginVo
     * @return
     */
    @RequestMapping("/create_token")
    @ResponseBody
    public Result<String> createToken(HttpServletResponse response, @Valid LoginVo loginVo) {
        log.info(loginVo.toString());
        String token = seckillUserService.login(response, loginVo);
        return Result.success(token);
    }

}

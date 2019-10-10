package com.sly.seckill.controller;

import com.sly.seckill.domain.SeckillOrder;
import com.sly.seckill.domain.SeckillUser;
import com.sly.seckill.result.Result;
import com.sly.seckill.service.SeckillUserService;
import com.sly.seckill.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/24 10:09
 * @package com.sly.seckill.controller
 **/
@Controller
@RequestMapping("/user")
public class UserController {

    // 日志记录：Logger是由slf4j接口规范创建的，对象有具体的实现类创建
    private static Logger log = LoggerFactory.getLogger(UserController.class);

    /**
     * 返回用户信息
     * @param user
     * @return
     */
    @RequestMapping("/user_info")
    @ResponseBody
    public Result<SeckillUser> userInfo(SeckillUser user){
        log.info(user.toString());
        return Result.success(user);
    }

}

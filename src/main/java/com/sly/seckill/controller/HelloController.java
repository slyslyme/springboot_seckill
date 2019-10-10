package com.sly.seckill.controller;


import com.sly.seckill.access.AccessLimit;
import com.sly.seckill.domain.User;
import com.sly.seckill.rabbitmq.MQSender;
import com.sly.seckill.redis.RedisService;
import com.sly.seckill.redis.UserKey;
import com.sly.seckill.result.CodeMsg;
import com.sly.seckill.result.Result;
import com.sly.seckill.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/23 9:37
 * @package com.sly.seckill.controller
 **/
@Controller
public class HelloController {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender mqSender;

    @ResponseBody
    @RequestMapping("/")
    public String index(){
        return "Hello world! Hello SpringBoot!";
    }

    // 1. rest api json输出 2.页面
    @RequestMapping("/hello")
    @ResponseBody
    public Result<String> hello(){
        return Result.success("hello, springboot");
    }

    @RequestMapping("/helloError")
    @ResponseBody
    public Result<String> helloError(){
        return Result.error(CodeMsg.SERVER_ERROR);
    }

    @RequestMapping("/thymeleaf")
    public String thymeleaf(Model model){
        model.addAttribute("name", "springboot!");
        return "hello"; // 返回客户端的HTML文件
    }

    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> dbGet(){
        User user = userService.getById(11);
        if(user != null)
            return Result.success(user);
        else
            return Result.error(CodeMsg.SERVER_ERROR);
    }

    // 事务验证
    @ResponseBody
    @RequestMapping("/db/tx")
    public Result<Boolean> dbTX(){
        boolean tx = userService.tx();
        return Result.success(tx);
    }

    // 测试RedisService的get方法
    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet() {
        User user = redisService.get(UserKey.getById, "" + 1, User.class);
        return Result.success(user);
    }

    // 测试RedisService的set方法
    @ResponseBody
    @RequestMapping("redis/set")
    public Result<Boolean> redisSet() {
        User user = new User();
        user.setId(1);
        user.setName("hahaha");
        boolean str = redisService.set(UserKey.getById,"" + 1, user);
        return Result.success(str);
    }

    @RequestMapping("/mqDirect")
    @ResponseBody
    public Result<String> mq(){
        mqSender.sendDirect("hello world, hello springboot + rabbitmq!!");
        return Result.success("hello, springboot");
    }

    @RequestMapping("/mqTopic")
    @ResponseBody
    public Result<String> mqTopic(){
        mqSender.sendTopic("hello world, hello springboot + rabbitmq!!");
        return Result.success("hello, springboot");
    }

    @RequestMapping("/mqFanout")
    @ResponseBody
    public Result<String> mqFanout(){
        mqSender.sendFanout("hello world!!");
        return Result.success("hello, springboot");
    }

    @RequestMapping("/mqHeader")
    @ResponseBody
    public Result<String> mqHeader(){
        mqSender.sendHeader("hello world!!");
        return Result.success("hello, springboot");
    }

    @AccessLimit(seconds = 5, maxCount = 5, needLogin = false)
    @RequestMapping("/limit")
    @ResponseBody
    public Result<String> limit(){
        return Result.success("OK");
    }
}


package com.sly.seckill.controller;

import com.sly.seckill.domain.OrderInfo;
import com.sly.seckill.domain.SeckillUser;
import com.sly.seckill.result.CodeMsg;
import com.sly.seckill.result.Result;
import com.sly.seckill.service.GoodsService;
import com.sly.seckill.service.OrderService;
import com.sly.seckill.vo.GoodsVo;
import com.sly.seckill.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/29 12:59
 * @package com.sly.seckill.controller
 **/
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> info(Model model, SeckillUser user, @RequestParam("orderId")long orderId){
        if(user == null)
            return Result.error(CodeMsg.SESSION_ERROR);

        // 获取订单信息
        OrderInfo orderInfo = orderService.getOrderById(orderId);
        if(orderInfo == null)
            return Result.error((CodeMsg.ORDER_NOT_EXIST));

        long goodsId = orderInfo.getGoodsId();
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo vo = new OrderDetailVo();
        vo.setGoods(goodsVo);
        vo.setOrder(orderInfo);
        return Result.success(vo);
    }

}

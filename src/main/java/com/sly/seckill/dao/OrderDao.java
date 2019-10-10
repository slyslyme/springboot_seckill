package com.sly.seckill.dao;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/25 20:41
 * @package com.sly.seckill.dao
 **/

import com.sly.seckill.domain.OrderInfo;
import com.sly.seckill.domain.SeckillOrder;
import org.apache.ibatis.annotations.*;

/**
 * seckill_order表数据访问层
 */
@Mapper
public interface OrderDao {


    /**
     * 通过用户id与商品id从订单列表中获取订单信息
     *
     * @param userId  用户id
     * @param goodsId 商品id
     * @return 秒杀订单信息
     */
    @Select("SELECT * FROM seckill_order WHERE user_id=#{userId} AND goods_id=#{goodsId}")
    SeckillOrder getSeckillOrderByUserIdAndGoodsId(@Param("userId") Long userId, @Param("goodsId") long goodsId);

    /**
     * 订单信息插入到Order_Info
     * @param orderInfo
     * @return
     */
    @Insert("INSERT INTO order_info (user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date)"
            + "VALUES (#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel},#{status},#{createDate} )")
    @SelectKey(keyColumn = "id", keyProperty = "id", resultType = long.class, before = false, statement = "SELECT last_insert_id()")
    Long insert(OrderInfo orderInfo);

    /**
     * 订单信息插入到seckill_order
     * @param seckillOrder
     */
    @Insert("INSERT INTO seckill_order(user_id, order_id, goods_id) VALUES (#{userId}, #{orderId}, #{goodsId})")
    void insertSeckillOrder(SeckillOrder seckillOrder);

    /**
     * 获取订单信息
     * @param orderId
     * @return
     */
    @Select("select * from order_info where id=#{orderId}")
    OrderInfo getOrderById(@Param("orderId") long orderId);
}
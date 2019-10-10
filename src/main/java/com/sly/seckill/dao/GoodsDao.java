package com.sly.seckill.dao;

import com.sly.seckill.domain.Goods;
import com.sly.seckill.domain.SeckillGoods;
import com.sly.seckill.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/25 11:13
 * @package com.sly.seckill.dao
 **/
@Mapper
public interface GoodsDao {
    /**
     * 查出商品信息（包含该商品的秒杀信息）
     * 利用左外连接(LEFT JOIN...ON...)的方式查
     * 所以GoodsVo的作用就是列举我们在连接中需要的列，方便我们查询所需的列，方便展示
     * @return
     */
    @Select("select g.*, mg.stock_count, mg.start_date, mg.end_date, " +
            "mg.seckill_price from seckill_goods mg left join goods g " +
            "on mg.goods_id = g.id")
    public List<GoodsVo> listGoodsVo();

    /**
     * 通过商品的id查出商品的所有信息（包含该商品的秒杀信息）
     * @param goodsId
     * @return
     */
    @Select("select g.*,mg.stock_count, mg.start_date, mg.end_date,mg.seckill_price " +
            "from seckill_goods mg left join goods g " +
            "on mg.goods_id = g.id where g.id = #{goodsId}")
    public GoodsVo getGoodsVoByGoodsId(@Param("goodsId") long goodsId);

    /**
     * 减库存
     * @param g
     */
    @Update("update seckill_goods set stock_count = stock_count - 1 where goods_id = #{goodsId} and stock_count > 0")
    int reduceStock(SeckillGoods g);
}

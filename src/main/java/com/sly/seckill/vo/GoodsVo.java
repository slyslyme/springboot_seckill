package com.sly.seckill.vo;

import com.sly.seckill.domain.Goods;

import java.util.Date;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/25 12:00
 * @package com.sly.seckill.vo
 **/

/**
 * 商品信息（并且包含商品的秒杀信息）（将两个表的数据合在一起）
 * 商品信息和商品的秒杀信息是存储在两个表中的（goods和seckill_goods）
 * 继承Goods便具有了goods表的信息，再额外添加上seckill_goods的信息即可
 */
public class GoodsVo extends Goods {

    /*只包含了部分seckill_goods表的信息*/
    private Double seckillPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;

    public Double getSeckillPrice() {
        return seckillPrice;
    }

    public void setSeckillPrice(Double seckillPrice) {
        this.seckillPrice = seckillPrice;
    }

    public Integer getStockCount() {
        return stockCount;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}

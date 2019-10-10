package com.sly.seckill.service;

import com.sly.seckill.dao.GoodsDao;
import com.sly.seckill.domain.Goods;
import com.sly.seckill.domain.SeckillGoods;
import com.sly.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/25 11:12
 * @package com.sly.seckill.service
 **/
@Service
public class GoodsService {
    @Autowired
    GoodsDao goodsDao;

    /**
     * 返回商品列表
     * @return
     */
    public List<GoodsVo> listGoodsVo(){
        return goodsDao.listGoodsVo();
    }

    /**
     * 获取单个商品
     * @param goodsId
     * @return
     */
    public GoodsVo getGoodsVoByGoodsId(long goodsId){
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    /**
     * 减库存
     * @param goods
     */
    public boolean reduceStock(GoodsVo goods) {
        SeckillGoods g = new SeckillGoods();
        g.setGoodsId(goods.getId());
        int res = goodsDao.reduceStock(g);
        return res > 0;
    }
}

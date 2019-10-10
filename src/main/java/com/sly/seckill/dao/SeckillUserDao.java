package com.sly.seckill.dao;

import com.sly.seckill.domain.SeckillUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/24 12:41
 * @package com.sly.seckill.dao
 **/
@Mapper
public interface SeckillUserDao {

    /**
     * 根据id查询秒杀用户信息
     * @param id
     * @return
     */
    @Select("select * from seckill_user where id=#{id}")
    public SeckillUser getById(@Param("id") long id);

    /**
     * 更新用户密码
     * @param updateUser
     */
    @Update("update seckill_user set password=#{password} where id=#{id}")
    void updatePassword(SeckillUser updateUser);
}

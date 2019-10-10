package com.sly.seckill.dao;

import com.sly.seckill.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/23 9:39
 * @package com.sly.seckill.dao
 **/
@Mapper
public interface UserDao {
    @Select("select * from user where id=#{id}")
    public User getById(int id);

    @Insert("insert into user(id, name) values(#{id}, #{name})")
    int insert(User user);
}

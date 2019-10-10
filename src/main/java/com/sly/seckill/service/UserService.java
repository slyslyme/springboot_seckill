package com.sly.seckill.service;

import com.sly.seckill.dao.UserDao;
import com.sly.seckill.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/23 9:38
 * @package com.sly.seckill.service
 **/
@Service
public class UserService {
    @Autowired
    UserDao userDao;

    public User getById(int id){
        User user = userDao.getById(id);
        return user;
    }

    @Transactional  // 验证事务
    public boolean tx() {
        User user = new User();
        user.setId(2);
        user.setName("cax");

        userDao.insert(user);

        User user1 = new User();
        user1.setId(1);
        user1.setName("fdfd");
        userDao.insert(user1);

        return true;
    }
}

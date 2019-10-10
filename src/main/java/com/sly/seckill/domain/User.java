package com.sly.seckill.domain;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/23 10:33
 * @package com.sly.seckill.domain
 **/
public class User {
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

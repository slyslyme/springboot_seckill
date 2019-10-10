package com.sly.seckill.util;

import java.util.UUID;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/24 20:26
 * @package com.sly.seckill.util
 **/
public class UUIDUtil {
    public static String uuid(){
        return UUID.randomUUID().toString().replace("-", "");
    }
}


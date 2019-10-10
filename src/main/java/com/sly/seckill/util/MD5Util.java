package com.sly.seckill.util;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/24 9:33
 * @package com.sly.seckill.util
 **/

import org.apache.commons.codec.digest.DigestUtils;

/**
 * MD5工具类
 */
public class MD5Util {

    // 盐值：用来拼接密码
    private static final String salt = "2dkj9i*37y2$*fu41y!%753^";

    /**
     * 获取输入串的MD5
     * @param src
     * @return
     */
    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    /**
     * 第一次MD5，把用户输入的密码进行拼接发送给服务端
     * @param inputPass
     * @return
     */
    public static String intputPassToServerPass(String inputPass){
        String str = "" + salt.charAt(0) + salt.charAt(3) + inputPass + salt.charAt(7) + salt.charAt(10);
        return md5(str);
    }

    /**
     * 第二次MD5，把服务端密码拼接为数据库密码
     * @param serverPass
     * @param salt
     * @return
     */
    public static String serverPassToDBPass(String serverPass, String salt){
        String DBPass = "" + salt.charAt(0) + salt.charAt(3) + serverPass + salt.charAt(7) + salt.charAt(10);
        return md5(DBPass);
    }

    /**
     * 两次MD5加密后的密码
     * @param inputPass
     * @param saltDB
     * @return
     */
    public static String inputPassToDBPass(String inputPass, String saltDB){
        String serverPass = intputPassToServerPass(inputPass);
        String DBPass = serverPassToDBPass(serverPass, saltDB);
        return DBPass;
    }

    public static void main(String args[]){
        //System.out.println(intputPassToServerPass("123456"));//80c22ed3884141e7fa940e3939cd8272
        //System.out.println(serverPassToDBPass(intputPassToServerPass("123456"), "2dkj9i*37y2$*fu41y!%753^")); // d553dc877288b31d69ef1e22cdf72392
        System.out.println(inputPassToDBPass("123456", "2dkj9i*37y2$*fu41y!%753^")); // d553dc877288b31d69ef1e22cdf72392
    }
}

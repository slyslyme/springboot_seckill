package com.sly.seckill.util;

import org.thymeleaf.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/24 12:27
 * @package com.sly.seckill.util
 **/
public class ValidatorUtil {
    private static final Pattern mobile_pattern = Pattern.compile("1\\d{10}");
    public static boolean isMobile(String src){
        if(StringUtils.isEmpty(src)) return false;
        Matcher m = mobile_pattern.matcher(src);
        return m.matches();
    }
}

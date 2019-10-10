package com.sly.seckill.validator;

import com.sly.seckill.util.ValidatorUtil;
import org.thymeleaf.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/24 15:31
 * @package com.sly.seckill.validator
 **/

/**
 * 真正用户手机号码检验的工具，会被注解@IsMobile所使用
 * 这个类需要实现Javax.validation.ConstraintValidator,否则不能被@Constraint参数使用
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {
    // 用于获取检验字段是否可以为空
    private boolean required = false;

    /**
     * 用于获取注解
     * @param constraintAnnotation
     */
    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    /**
     * 用于检验字段是否合法
     * @param s
     * @param constraintValidatorContext
     * @return
     */
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(required) // 必须
            return ValidatorUtil.isMobile(s); // 检验结果
        else{
            if(StringUtils.isEmpty(s))
                return true;
            else
                return ValidatorUtil.isMobile(s); // 校验结果
        }
    }
}

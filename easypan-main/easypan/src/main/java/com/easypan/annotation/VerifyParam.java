package com.easypan.annotation;

import com.easypan.entity.enums.VerifyRegexEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 参数校验注解，用于方法参数或类字段上，以验证输入数据的合法性
 * 提供了对参数的最小值、最大值、是否必填以及正则表达式校验的配置
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface VerifyParam {
    /**
     * 参数的最小值，默认为-1，表示不做最小值校验
     * 当设置具体值时，会校验参数是否大于等于该值
     */
    int min() default -1;

    /**
     * 参数的最大值，默认为-1，表示不做最大值校验
     * 当设置具体值时，会校验参数是否小于等于该值
     */
    int max() default -1;

    /**
     * 参数是否必填，默认为false，表示非必填
     * 当设置为true时，会校验参数是否为空或为null
     */
    boolean required() default false;

    /**
     * 参数的正则表达式校验规则，默认为NO，表示不进行正则校验
     * 提供了多种预定义的正则表达式供选择，用于复杂的数据格式校验
     */
    VerifyRegexEnum regex() default VerifyRegexEnum.NO;
}

package com.easypan.annotation;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

/**
 * 自定义全局拦截器注解
 * 用于在方法级别上标记需要应用全局拦截器的行为
 * 主要关注参数校验、登录状态校验和管理员权限校验
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface GlobalInterceptor {
    /**
     * 校验参数
     * 控制是否需要对请求参数进行校验
     * 默认值为 false，表示不进行参数校验
     *
     * @return 是否需要进行参数校验
     */
    boolean checkParams() default false;

    /**
     * 校验登录
     * 控制是否需要检查用户登录状态
     * 默认值为 true，表示需要进行登录状态校验
     *
     * @return 是否需要进行登录状态校验
     */
    boolean checkLogin() default true;

    /**
     * 校验超级管理员
     * 控制是否需要检查用户是否为超级管理员
     * 默认值为 false，表示不需要进行管理员权限校验
     *
     * @return 是否需要进行管理员权限校验
     */
    boolean checkAdmin() default false;
}

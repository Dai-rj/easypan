package com.easypan.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * 邮箱验证码 数据库操作接口
 * 继承自BaseMapper，提供了对邮箱验证码相关数据的基本操作
 * 包括根据邮箱和验证码更新、删除和获取对象的特定操作
 */
public interface EmailCodeMapper<T, P> extends BaseMapper<T, P> {

    /**
     * 根据邮箱和验证码更新记录
     * 用于在验证用户身份后更新邮箱验证码记录
     *
     * @param email 用户邮箱
     * @param code 验证码
     * @return 受影响的行数
     */
    Integer updateByEmailAndCode(@Param("bean") T t, @Param("email") String email, @Param("code") String code);

    /**
     * 根据邮箱和验证码删除记录
     * 用于在验证用户身份后删除邮箱验证码记录
     *
     * @param email 用户邮箱
     * @param code 验证码
     * @return 受影响的行数
     */
    Integer deleteByEmailAndCode(@Param("email") String email, @Param("code") String code);

    /**
     * 根据邮箱和验证码获取对象
     * 用于获取特定的邮箱验证码记录
     *
     * @param email 用户邮箱
     * @param code 验证码
     * @return 对应的邮箱验证码对象
     */
    T selectByEmailAndCode(@Param("email") String email, @Param("code") String code);

    /**
     * 禁用特定邮箱的验证码
     * 用于在用户使用验证码登录或注册后，禁用该邮箱的验证码
     *
     * @param email 用户邮箱
     */
    void disableEmailCode(@Param("email") String email);
}

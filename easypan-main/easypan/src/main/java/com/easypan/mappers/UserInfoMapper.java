package com.easypan.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * 用户信息 数据库操作接口
 * 继承BaseMapper，扩展了更多用户信息相关的数据库操作
 */
public interface UserInfoMapper<T, P> extends BaseMapper<T, P> {

    /**
     * 根据UserId更新用户信息
     *
     * @param userId 用户ID
     * @return 更新影响的行数
     */
    Integer updateByUserId(@Param("bean") T t, @Param("userId") String userId);

    /**
     * 根据UserId删除用户信息
     *
     * @param userId 用户ID
     * @return 删除影响的行数
     */
    Integer deleteByUserId(@Param("userId") String userId);

    /**
     * 根据UserId获取用户信息对象
     *
     * @param userId 用户ID
     * @return 用户信息对象
     */
    T selectByUserId(@Param("userId") String userId);

    /**
     * 根据Email更新用户信息
     *
     * @param email 用户邮箱
     * @return 更新影响的行数
     */
    Integer updateByEmail(@Param("bean") T t, @Param("email") String email);

    /**
     * 根据Email删除用户信息
     *
     * @param email 用户邮箱
     * @return 删除影响的行数
     */
    Integer deleteByEmail(@Param("email") String email);

    /**
     * 根据Email获取用户信息对象
     *
     * @param email 用户邮箱
     * @return 用户信息对象
     */
    T selectByEmail(@Param("email") String email);

    /**
     * 根据QqOpenId更新用户信息
     *
     * @param qqOpenId 用户的QQ OpenId
     * @return 更新影响的行数
     */
    Integer updateByQqOpenId(@Param("bean") T t, @Param("qqOpenId") String qqOpenId);

    /**
     * 根据QqOpenId删除用户信息
     *
     * @param qqOpenId 用户的QQ OpenId
     * @return 删除影响的行数
     */
    Integer deleteByQqOpenId(@Param("qqOpenId") String qqOpenId);

    /**
     * 根据QqOpenId获取用户信息对象
     *
     * @param qqOpenId 用户的QQ OpenId
     * @return 用户信息对象
     */
    T selectByQqOpenId(@Param("qqOpenId") String qqOpenId);

    /**
     * 根据NickName更新用户信息
     *
     * @param nickName 用户昵称
     * @return 更新影响的行数
     */
    Integer updateByNickName(@Param("bean") T t, @Param("nickName") String nickName);

    /**
     * 根据NickName删除用户信息
     *
     * @param nickName 用户昵称
     * @return 删除影响的行数
     */
    Integer deleteByNickName(@Param("nickName") String nickName);

    /**
     * 根据NickName获取用户信息对象
     *
     * @param nickName 用户昵称
     * @return 用户信息对象
     */
    T selectByNickName(@Param("nickName") String nickName);

    /**
     * 更新用户的存储空间信息
     *
     * @param userId    用户ID
     * @param useSpace  用户已使用的空间大小
     * @param totalSpace 用户总的空间大小
     * @return 更新影响的行数
     */
    Integer updateUserSpace(@Param("userId") String userId,
                            @Param("useSpace") Long useSpace,
                            @Param("totalSpace") Long totalSpace);
}

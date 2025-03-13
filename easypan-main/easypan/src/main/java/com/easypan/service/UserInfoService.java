package com.easypan.service;

import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.po.UserInfo;
import com.easypan.entity.query.UserInfoQuery;
import com.easypan.entity.vo.PaginationResultVO;

import java.util.List;

/**
 * 用户信息 业务接口
 */
public interface UserInfoService {

    /**
     * 根据条件查询列表
     *
     * @param param 查询条件
     * @return 用户信息列表
     */
    List<UserInfo> findListByParam(UserInfoQuery param);

    /**
     * 根据条件查询总数
     *
     * @param param 查询条件
     * @return 用户信息总数
     */
    Integer findCountByParam(UserInfoQuery param);

    /**
     * 分页查询
     *
     * @param param 分页查询条件
     * @return 分页查询结果
     */
    PaginationResultVO<UserInfo> findListByPage(UserInfoQuery param);

    /**
     * 新增用户信息
     *
     * @param bean 用户信息对象
     * @return 新增结果
     */
    Integer add(UserInfo bean);

    /**
     * 批量新增用户信息
     *
     * @param listBean 用户信息对象列表
     * @return 批量新增结果
     */
    Integer addBatch(List<UserInfo> listBean);

    /**
     * 批量新增或修改用户信息
     *
     * @param listBean 用户信息对象列表
     * @return 批量新增或修改结果
     */
    Integer addOrUpdateBatch(List<UserInfo> listBean);

    /**
     * 多条件更新用户信息
     *
     * @param bean    用户信息对象
     * @param param   查询条件
     * @return 更新结果
     */
    Integer updateByParam(UserInfo bean, UserInfoQuery param);

    /**
     * 多条件删除用户信息
     *
     * @param param 查询条件
     * @return 删除结果
     */
    Integer deleteByParam(UserInfoQuery param);

    /**
     * 根据UserId查询用户信息
     *
     * @param userId 用户ID
     * @return 用户信息对象
     */
    UserInfo getUserInfoByUserId(String userId);

    /**
     * 根据UserId修改用户信息
     *
     * @param bean    用户信息对象
     * @param userId 用户ID
     * @return 修改结果
     */
    Integer updateUserInfoByUserId(UserInfo bean, String userId);

    /**
     * 根据UserId删除用户信息
     *
     * @param userId 用户ID
     * @return 删除结果
     */
    Integer deleteUserInfoByUserId(String userId);

    /**
     * 根据Email查询用户信息
     *
     * @param email 用户邮箱
     * @return 用户信息对象
     */
    UserInfo getUserInfoByEmail(String email);

    /**
     * 根据Email修改用户信息
     *
     * @param bean   用户信息对象
     * @param email 用户邮箱
     * @return 修改结果
     */
    Integer updateUserInfoByEmail(UserInfo bean, String email);

    /**
     * 根据Email删除用户信息
     *
     * @param email 用户邮箱
     * @return 删除结果
     */
    Integer deleteUserInfoByEmail(String email);

    /**
     * 根据QqOpenId查询用户信息
     *
     * @param qqOpenId QQ开放平台ID
     * @return 用户信息对象
     */
    UserInfo getUserInfoByQqOpenId(String qqOpenId);

    /**
     * 根据QqOpenId修改用户信息
     *
     * @param bean      用户信息对象
     * @param qqOpenId QQ开放平台ID
     * @return 修改结果
     */
    Integer updateUserInfoByQqOpenId(UserInfo bean, String qqOpenId);

    /**
     * 根据QqOpenId删除用户信息
     *
     * @param qqOpenId QQ开放平台ID
     * @return 删除结果
     */
    Integer deleteUserInfoByQqOpenId(String qqOpenId);

    /**
     * 根据NickName查询用户信息
     *
     * @param nickName 用户昵称
     * @return 用户信息对象
     */
    UserInfo getUserInfoByNickName(String nickName);

    /**
     * 根据NickName修改用户信息
     *
     * @param bean      用户信息对象
     * @param nickName 用户昵称
     * @return 修改结果
     */
    Integer updateUserInfoByNickName(UserInfo bean, String nickName);

    /**
     * 根据NickName删除用户信息
     *
     * @param nickName 用户昵称
     * @return 删除结果
     */
    Integer deleteUserInfoByNickName(String nickName);

    /**
     * 用户注册
     *
     * @param email    用户邮箱
     * @param nickName 用户昵称
     * @param password 用户密码
     * @param emailCode 邮箱验证码
     */
    void register(String email, String nickName, String password, String emailCode);

    /**
     * 用户登录
     *
     * @param email    用户邮箱
     * @param password 用户密码
     * @return 登录结果
     */
    SessionWebUserDto login(String email, String password);

    /**
     * 重置密码
     *
     * @param email    用户邮箱
     * @param password 新密码
     * @param emailCode 邮箱验证码
     */
    void resetPwd(String email, String password, String emailCode);
}

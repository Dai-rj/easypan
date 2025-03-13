package com.easypan.entity.query;


import lombok.Getter;
import lombok.Setter;

/**
 * 用户信息参数
 */
@Setter
@Getter
public class UserInfoQuery extends BaseParam {


    /**
     * 用户ID
     */
    private String userId;

    private String userIdFuzzy;

    /**
     * 昵称
     */
    private String nickName;

    private String nickNameFuzzy;

    /**
     * 邮箱
     */
    private String email;

    private String emailFuzzy;

    /**
     * qqOpenId
     */
    private String qqOpenId;

    private String qqOpenIdFuzzy;

    /**
     * qq头像
     */
    private String qqAvatar;

    private String qqAvatarFuzzy;

    /**
     * 密码
     */
    private String password;

    private String passwordFuzzy;

    /**
     * 加入时间
     */
    private String joinTime;

    private String joinTimeStart;

    private String joinTimeEnd;

    /**
     * 最后登录时间
     */
    private String lastLoginTime;

    private String lastLoginTimeStart;

    private String lastLoginTimeEnd;

    /**
     * 0：禁用1：启用
     */
    private Integer status;

    /**
     * 使用空间单位byte
     */
    private Long useSpace;

    /**
     * 总空间
     */
    private Long totalSpace;
}

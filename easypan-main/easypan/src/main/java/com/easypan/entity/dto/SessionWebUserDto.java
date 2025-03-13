package com.easypan.entity.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * SessionWebUserDto类用于表示Web用户的相关信息，主要用于会话中存储用户数据
 * 它提供了用户的昵称、用户ID、管理员状态和头像URL等属性的获取和设置方法
 */
@Getter
@Setter
public class SessionWebUserDto {

    /**
     * 用户昵称，用于在界面上显示用户的名称
     */
    private String nickName;

    /**
     * 用户ID，唯一标识一个用户
     */
    private String userId;

    /**
     * 管理员状态，指示用户是否为管理员
     * 管理员可能拥有更多的系统权限或功能
     */
    private Boolean isAdmin;

    /**
     * 用户头像的URL地址，用于在界面上显示用户头像
     */
    private String avatar;

}

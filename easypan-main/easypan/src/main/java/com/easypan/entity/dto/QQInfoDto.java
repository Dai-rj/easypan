package com.easypan.entity.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * QQ信息DTO类，用于存储从QQ开放平台获取的用户信息
 */
@Setter
@Getter
public class QQInfoDto {
    /**
     * QQ开放平台返回的状态码，0表示成功，非0表示失败
     */
    private Integer ret;

    /**
     * QQ开放平台返回的消息，描述请求结果
     */
    private String msg;

    /**
     * 用户的QQ昵称
     */
    private String nickname;

    /**
     * 用户的QQ头像URL，尺寸较小，适用于需要较小图标的地方
     */
    private String figureurl_qq_1;

    /**
     * 用户的QQ头像URL，尺寸适中，适用于大多数显示需求
     */
    private String figureurl_qq_2;

    /**
     * 用户的性别，通常为'M'（男）或'F'（女）
     */
    private String gender;
}

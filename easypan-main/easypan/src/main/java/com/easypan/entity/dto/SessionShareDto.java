package com.easypan.entity.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * SessionShareDto类用于表示分享文件时的会话信息
 * 它封装了与分享会话相关的数据，如分享ID、分享用户ID、过期时间和文件ID
 */
@Setter
@Getter
public class SessionShareDto {
    /**
     * 分享ID，唯一标识一次分享会话
     */
    private String shareId;

    /**
     * 分享用户ID，标识发起分享的用户
     */
    private String shareUserId;

    /**
     * 过期时间，表示分享会话的有效期限
     * 超过这个时间后，分享链接将无法访问
     */
    private Date expireTime;

    /**
     * 文件ID，标识被分享的文件
     */
    private String fileId;
}

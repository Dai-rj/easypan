package com.easypan.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 系统设置数据传输对象
 * 用于序列化和反序列化系统设置的相关信息
 * 实现了Serializable接口，以支持对象的序列化和反序列化
 */
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SysSettingsDto implements Serializable {

    /**
     * 注册邮件标题
     * 默认值为"邮箱验证码"
     */
    private String registerMailTitle = "邮箱验证码";

    /**
     * 注册邮件内容
     * 默认值为"您好，您的邮箱验证码是：%s, 15分钟有效"
     * %s为占位符，用于插入验证码
     */
    private String registerEmailContent = "您好，您的邮箱验证码是：%s, 15分钟有效";

    /**
     * 用户初始使用空间
     * 以GB为单位
     * 默认值为5GB
     */
    private Integer userInitUseSpace = 5;

}

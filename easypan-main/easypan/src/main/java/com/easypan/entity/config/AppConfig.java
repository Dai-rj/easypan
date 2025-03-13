package com.easypan.entity.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// 定义一个组件类AppConfig，用于存储应用程序配置
@Getter
@Component("appConfig")
public class AppConfig {
    // 获取发件人用户名
    // 从配置文件中获取发件人用户名，如果没有配置，则默认为空字符串
    @Value("${spring.mail.username:}")
    private String sendUserName;

    // 获取管理员邮箱地址
    // 从配置文件中获取管理员邮箱地址，如果没有配置，则默认为空字符串
    @Value("${admin.emails:}")
    private String adminEmails;

    // 获取项目文件夹路径
    // 从配置文件中获取项目文件夹路径，如果没有配置，则默认为空字符串
    @Value("${project.folder:}")
    private String projectFolder;

}

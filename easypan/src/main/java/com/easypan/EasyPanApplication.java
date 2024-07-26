package com.easypan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//  EasyPan应用程序的入口类。
//  启用了以下特性：
//  1. 异步方法执行：通过@EnableAsync注解，使得应用程序能够支持异步方法的执行，提高应用程序的响应性能。
//  2. 事务管理：通过@EnableTransactionManagement注解，启用了Spring的事务管理功能，能够对方法执行进行事务控制，确保数据的一致性。
//  3. 定时任务：通过@EnableScheduling注解，启用了Spring的定时任务功能，能够定期执行特定的任务。
//  @SpringBootApplication 注解用于标记这个类是一个Spring Boot应用程序的入口点。
//  @param scanBasePackages 指定Spring Boot自动扫描组件的包路径，这里设置为"com.easypan"，表示自动扫描该包及其子包下的所有组件。

@EnableAsync
@EnableTransactionManagement
@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.easypan")
public class EasyPanApplication {

//     应用程序的入口方法。
//     使用SpringApplication.run方法启动Spring Boot应用程序，传入EasyPanApplication.class和程序启动参数。
//     @param args 程序启动参数

    public static void main(String[] args) {
        SpringApplication.run(EasyPanApplication.class, args);
    }

}

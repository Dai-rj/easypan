package com.easypan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


// * @EnableAsync 启用异步方法执行，允许方法在不同的线程中异步运行。
// * @SpringBootApplication 自动配置Spring Boot应用程序，扫描包com.easypan。
// * @MapperScan 扫描MyBatis映射器接口，自动配置MyBatis-Spring。
// * @EnableTransactionManagement 启用Spring的事务管理功能。
// * @EnableScheduling 启用计划任务，允许在应用程序中安排任务。
@EnableAsync
@SpringBootApplication(scanBasePackages = {"com.easypan"})
@MapperScan(basePackages = {"com.easypan.mappers"})
@EnableTransactionManagement
@EnableScheduling
public class EasyPanApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasyPanApplication.class, args);
    }
}
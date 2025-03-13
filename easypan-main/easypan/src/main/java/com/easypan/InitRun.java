package com.easypan;

import com.easypan.component.RedisComponent;
import com.easypan.exception.BusinessException;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component("initRun")
public class InitRun implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(InitRun.class);

    @Resource
    private DataSource dataSource;

    @Resource
    private RedisComponent redisComponent;

    /**
     * 启动应用时执行的一些初始化操作
     * 此方法主要用于在应用启动后，进行数据库和Redis连接的初始化和验证
     * 通过调用dataSource.getConnection()和redisComponent.getSysSettingsDto()来验证数据库和Redis的连接是否正常
     * 如果连接成功，表示服务启动成功，否则抛出业务异常
     *
     * @param args 应用启动参数，包含未分组的参数和命名参数
     * @throws BusinessException 当数据库或Redis连接失败时抛出此异常，表示服务启动失败
     */
    @Override
    public void run(ApplicationArguments args) {
        try {
            // 验证数据库连接是否正常
            dataSource.getConnection();
            // 验证Redis连接是否正常，并获取系统设置信息
            redisComponent.getSysSettingsDto();
            // 日志记录服务启动成功信息
            logger.error("服务启动成功，可以开始愉快的开发了");
        } catch (Exception e) {
            // 当数据库或Redis连接失败时，记录错误日志并抛出业务异常
            logger.error("数据库或者redis设置失败，请检查配置");
            throw new BusinessException("服务启动失败");
        }
    }
}

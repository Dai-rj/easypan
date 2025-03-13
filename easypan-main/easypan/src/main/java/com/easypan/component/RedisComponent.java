package com.easypan.component;

import com.easypan.entity.constants.Constants;
import com.easypan.entity.dto.SysSettingsDto;
import com.easypan.entity.dto.UserSpaceDto;
import com.easypan.entity.po.FileInfo;
import com.easypan.entity.query.FileInfoQuery;
import com.easypan.mappers.FileInfoMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * Redis组件，用于处理与Redis相关的操作
 * 包括系统设置、用户空间使用情况和临时文件大小的缓存处理
 */
@Component("redisComponent")
public class RedisComponent {
    // 注入Redis工具类，用于Redis操作
    @Resource
    private RedisUtils redisUtils;

    // 注入FileInfoMapper，用于文件信息的数据库操作
    @Resource
    private FileInfoMapper<FileInfo, FileInfoQuery> fileInfoMapper;

    /**
     * 获取系统设置信息
     * 首先尝试从Redis中获取，如果获取失败则创建新实例并存入Redis
     *
     * @return SysSettingsDto 系统设置信息对象
     */
    public SysSettingsDto getSysSettingsDto() {
        // 从Redis中获取系统设置信息
        SysSettingsDto sysSettingsDto = (SysSettingsDto) redisUtils.get(Constants.REDIS_KEY_SYS_SETTING);
        if (sysSettingsDto == null) {
            // 如果Redis中不存在系统设置信息，则创建新实例
            sysSettingsDto = new SysSettingsDto();
            // 将新创建的系统设置信息实例存入Redis
            redisUtils.set(Constants.REDIS_KEY_SYS_SETTING, sysSettingsDto);
        }
        return sysSettingsDto;
    }

    /**
     * 保存用户空间使用信息到Redis
     * 使用带有过期时间的设置，过期时间常量为REDIS_KEY_EXPIRES_DAY
     *
     * @param userId 用户ID
     * @param userSpaceDto 用户空间使用信息对象
     */
    public void saveUserSpaceUse(String userId, UserSpaceDto userSpaceDto) {
        redisUtils.sites(Constants.REDIS_KEY_USER_SPACE_USE + userId, userSpaceDto, Constants.REDIS_KEY_EXPIRES_DAY);
    }

    /**
     * 获取用户空间使用信息
     * 首先尝试从Redis中获取，如果获取失败则从数据库中查询并存入Redis
     *
     * @param userId 用户ID
     * @return UserSpaceDto 用户空间使用信息对象
     */
    public UserSpaceDto getUserSpaceDto(String userId) {
        // 从Redis中获取用户空间使用信息
        UserSpaceDto spaceDto = (UserSpaceDto) redisUtils.get(Constants.REDIS_KEY_USER_SPACE_USE + userId);
        if (spaceDto == null) {
            // 如果Redis中不存在该用户的空间使用信息，则初始化UserSpaceDto对象
            spaceDto = new UserSpaceDto();
            // 从数据库中查询用户已使用的空间大小
            Long useSpace = fileInfoMapper.selectUseSpace(userId);
            spaceDto.setUseSpace(useSpace);
            // 设置用户的总空间大小，这里使用系统设置中的用户初始空间大小
            spaceDto.setTotalSpace(getSysSettingsDto().getUserInitUseSpace() * Constants.MB);
            // 将用户空间使用信息保存到Redis中
            saveUserSpaceUse(userId, spaceDto);
        }
        return spaceDto;
    }

    /**
     * 保存文件临时大小到Redis
     * 将当前大小与新文件大小相加后存入Redis，并设置过期时间为一小时
     *
     * @param userId 用户ID
     * @param fileId 文件ID
     * @param fileSize 文件大小
     */
    public void saveFileTempSize(String userId, String fileId, Long fileSize) {
        // 获取当前文件的临时大小
        Long currentSize = getFileTempSize(userId, fileId);
        // 将当前大小与新文件大小相加，并将结果存入Redis
        // 使用用户ID和文件ID作为键的一部分，以确保键的唯一性
        // 设置过期时间为一小时，以避免临时大小数据长时间占用内存
        redisUtils.sites(Constants.REDIS_KEY_USER_FILE_TEMP_SIZE + userId + fileId, currentSize + fileSize, Constants.REDIS_KEY_EXPIRES_ONE_HOUR);
    }

    /**
     * 获取临时文件大小
     * 从Redis中获取指定键的文件大小，如果键不存在则返回0
     *
     * @param userId 用户ID
     * @param fileId 文件ID
     * @return Long 临时文件大小
     */
    public Long getFileTempSize(String userId, String fileId) {
        return getFileSizeFromRedis(Constants.REDIS_KEY_USER_FILE_TEMP_SIZE + userId + fileId);
    }

    /**
     * 从Redis中获取文件大小
     * 根据键类型转换为Long类型，如果键不存在或类型不匹配则返回0
     *
     * @param key Redis键
     * @return Long 文件大小
     */
    private Long getFileSizeFromRedis(String key) {
        // 从Redis中获取键对应的值
        Object sizeObj = redisUtils.get(key);
        // 如果键不存在，则返回0
        if (sizeObj == null) {
            return 0L;
        }
        // 如果值是Integer类型，则转换为Long类型并返回
        if (sizeObj instanceof Integer) {
            return ((Integer) sizeObj).longValue();
        } else if (sizeObj instanceof Long) {
            // 如果值是Long类型，则直接返回
            return (Long) sizeObj;
        }
        // 如果值不是预期的类型，则返回0
        return 0L;
    }

}

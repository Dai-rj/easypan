package com.easypan.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * 文件信息 数据库操作接口
 * 继承自BaseMapper，提供了特定于文件信息的数据库操作方法
 */
public interface FileInfoMapper<T, P> extends BaseMapper<T, P> {

    /**
     * 根据文件ID和用户ID更新文件信息
     *
     * @param fileId 文件ID
     * @param userId 用户ID
     * @return 更新操作影响的行数
     */
    Integer updateByFileIdAndUserId(@Param("bean") T t, @Param("fileId") String fileId, @Param("userId") String userId);

    /**
     * 根据文件ID和用户ID删除文件信息
     *
     * @param fileId 文件ID
     * @param userId 用户ID
     * @return 删除操作影响的行数
     */
    Integer deleteByFileIdAndUserId(@Param("fileId") String fileId, @Param("userId") String userId);

    /**
     * 根据文件ID和用户ID获取文件信息对象
     *
     * @param fileId 文件ID
     * @param userId 用户ID
     * @return 文件信息对象，如果不存在则返回null
     */
    T selectByFileIdAndUserId(@Param("fileId") String fileId, @Param("userId") String userId);

    /**
     * 查询用户的已使用空间
     *
     * @param userId 用户ID
     * @return 用户的已使用空间大小
     */
    Long selectUseSpace(@Param("userId") String userId);
}

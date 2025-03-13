package com.easypan.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * 分享信息 数据库操作接口
 * 继承自BaseMapper，提供了针对分享信息的通用数据库操作
 * @param <T> 泛型，表示可以操作的实体类型
 * @param <P> 泛型，表示可以操作的主键类型
 */
public interface FileShareMapper<T, P> extends BaseMapper<T, P> {

    /**
     * 根据ShareId更新实体信息
     * @param t 要更新的实体对象
     * @param shareId 实体的分享ID
     * @return 受影响的行数
     */
    Integer updateByShareId(@Param("bean") T t, @Param("shareId") String shareId);

    /**
     * 根据ShareId删除实体信息
     * @param shareId 实体的分享ID
     * @return 受影响的行数
     */
    Integer deleteByShareId(@Param("shareId") String shareId);

    /**
     * 根据ShareId获取实体对象
     * @param shareId 实体的分享ID
     * @return 实体对象，如果找不到则返回null
     */
    T selectByShareId(@Param("shareId") String shareId);

    /**
     * 批量删除分享信息
     * @param shareIdArray 要删除的分享ID数组
     * @param userId 执行删除操作的用户ID
     * @return 受影响的行数
     */
    Integer deleteFileShareBatch(@Param("shareIdArray") String[] shareIdArray, @Param("userId") String userId);

    /**
     * 更新分享信息的展示次数
     * @param shareId 分享ID
     */
    void updateShareShowCount(@Param("shareId") String shareId);
}

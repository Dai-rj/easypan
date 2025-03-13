package com.easypan.mappers;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * BaseMapper: 定义了通用的数据库操作接口，包括增删查改等基本操作
 * T: 表示实体类类型
 * P: 表示查询参数类型
 */
interface BaseMapper<T, P> {

    /**
     * selectList: 根据参数查询集合
     * @return 实体类的集合
     */
    List<T> selectList(@Param("query") P p);

    /**
     * selectCount: 根据集合查询数量
     * @return 实体类的数量
     */
    Integer selectCount(@Param("query") P p);

    /**
     * insert: 插入
     * @return 影响的行数
     */
    Integer insert(@Param("bean") T t);

    /**
     * insertOrUpdate: 插入或者更新
     * @return 影响的行数
     */
    Integer insertOrUpdate(@Param("bean") T t);

    /**
     * insertBatch: 批量插入
     * @param list 要插入的实体类对象集合
     * @return 影响的行数
     */
    Integer insertBatch(@Param("list") List<T> list);

    /**
     * insertOrUpdateBatch: 批量插入或更新
     * @param list 要插入或更新的实体类对象集合
     * @return 影响的行数
     */
    Integer insertOrUpdateBatch(@Param("list") List<T> list);

    /**
     * updateByParams: 多条件更新
     * @return 影响的行数
     */
    Integer updateByParam(@Param("bean") T t, @Param("query") P p);

    /**
     * deleteByParam: 多条件删除
     * @return 影响的行数
     */
    Integer deleteByParam(@Param("query") P p);
}

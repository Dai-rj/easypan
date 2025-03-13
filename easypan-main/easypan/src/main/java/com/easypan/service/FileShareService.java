package com.easypan.service;

import com.easypan.entity.dto.SessionShareDto;
import com.easypan.entity.po.FileShare;
import com.easypan.entity.query.FileShareQuery;
import com.easypan.entity.vo.PaginationResultVO;

import java.util.List;

/**
 * 分享信息 业务接口
 */
public interface FileShareService {

    /**
     * 根据条件查询列表
     *
     * @param param 查询参数
     * @return 符合条件的文件分享列表
     */
    List<FileShare> findListByParam(FileShareQuery param);

    /**
     * 根据条件查询计数
     *
     * @param param 查询参数
     * @return 符合条件的文件分享数量
     */
    Integer findCountByParam(FileShareQuery param);

    /**
     * 分页查询
     *
     * @param param 分页查询参数
     * @return 分页结果对象，包含文件分享列表和分页信息
     */
    PaginationResultVO<FileShare> findListByPage(FileShareQuery param);

    /**
     * 新增文件分享记录
     *
     * @param bean 待新增的文件分享对象
     * @return 新增影响的行数
     */
    Integer add(FileShare bean);

    /**
     * 批量新增文件分享记录
     *
     * @param listBean 待新增的文件分享对象列表
     * @return 新增影响的行数
     */
    Integer addBatch(List<FileShare> listBean);

    /**
     * 批量新增或修改文件分享记录
     *
     * @param listBean 待新增或修改的文件分享对象列表
     * @return 新增或修改影响的行数
     */
    Integer addOrUpdateBatch(List<FileShare> listBean);

    /**
     * 根据ShareId查询文件分享对象
     *
     * @param shareId 分享ID
     * @return 对应分享ID的文件分享对象
     */
    FileShare getFileShareByShareId(String shareId);

    /**
     * 根据ShareId修改文件分享信息
     *
     * @param bean    待修改的文件分享对象
     * @param shareId 分享ID
     * @return 修改影响的行数
     */
    Integer updateFileShareByShareId(FileShare bean, String shareId);

    /**
     * 根据ShareId删除文件分享记录
     *
     * @param shareId 分享ID
     * @return 删除影响的行数
     */
    Integer deleteFileShareByShareId(String shareId);

    /**
     * 保存文件分享信息（新增或更新）
     *
     * @param share 待保存的文件分享对象
     */
    void saveShare(FileShare share);

    /**
     * 批量删除文件分享记录
     *
     * @param shareIdArray 待删除的分享ID数组
     * @param userId       执行删除操作的用户ID
     */
    void deleteFileShareBatch(String[] shareIdArray, String userId);

    /**
     * 验证分享码是否正确
     *
     * @param shareId 分享ID
     * @param code    用户输入的分享码
     * @return 如果分享码正确，则返回会话分享DTO对象
     */
    SessionShareDto checkShareCode(String shareId, String code);
}

package com.easypan.service;

import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.dto.UploadResultDto;
import com.easypan.entity.po.FileInfo;
import com.easypan.entity.query.FileInfoQuery;
import com.easypan.entity.vo.PaginationResultVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件信息 业务接口
 */
public interface FileInfoService {

    /**
     * 根据条件查询列表
     *
     * @param param 查询条件
     * @return 符合条件的文件信息列表
     */
    List<FileInfo> findListByParam(FileInfoQuery param);

    /**
     * 根据条件查询列表
     *
     * @param param 查询条件
     * @return 符合条件的文件信息数量
     */
    Integer findCountByParam(FileInfoQuery param);

    /**
     * 分页查询
     *
     * @param param 分页查询条件
     * @return 分页后的文件信息结果集
     */
    PaginationResultVO<FileInfo> findListByPage(FileInfoQuery param);

    /**
     * 新增
     *
     * @param bean 待新增的文件信息对象
     * @return 新增的记录数
     */
    Integer add(FileInfo bean);

    /**
     * 批量新增
     *
     * @param listBean 待新增的文件信息对象列表
     * @return 新增的记录数
     */
    Integer addBatch(List<FileInfo> listBean);

    /**
     * 批量新增/修改
     *
     * @param listBean 待新增或修改的文件信息对象列表
     * @return 新增或修改的记录数
     */
    Integer addOrUpdateBatch(List<FileInfo> listBean);

    /**
     * 多条件更新
     *
     * @param bean    待更新的文件信息对象
     * @param param   更新条件
     * @return 更新的记录数
     */
    Integer updateByParam(FileInfo bean, FileInfoQuery param);

    /**
     * 多条件删除
     *
     * @param param 删除条件
     * @return 删除的记录数
     */
    Integer deleteByParam(FileInfoQuery param);

    /**
     * 根据FileIdAndUserId查询对象
     *
     * @param fileId 文件ID
     * @param userId 用户ID
     * @return 文件信息对象
     */
    FileInfo getFileInfoByFileIdAndUserId(String fileId, String userId);

    /**
     * 根据FileIdAndUserId修改
     *
     * @param bean    待修改的文件信息对象
     * @param fileId  文件ID
     * @param userId  用户ID
     * @return 修改的记录数
     */
    Integer updateFileInfoByFileIdAndUserId(FileInfo bean, String fileId, String userId);

    /**
     * 根据FileIdAndUserId删除
     *
     * @param fileId 文件ID
     * @param userId 用户ID
     * @return 删除的记录数
     */
    Integer deleteFileInfoByFileIdAndUserId(String fileId, String userId);

    /**
     * 上传文件的方法
     *
     * @param webUserDto 用户会话信息，包含用户身份认证等信息
     * @param fileId     文件的唯一标识符
     * @param file       待上传的文件，以MultipartFile形式传入
     * @param fileName   文件的原始名称
     * @param filePid    文件的父级标识（如果有的话），可能用于构建文件路径或层级关系
     * @param fileMd5    文件的MD5校验和，用于验证文件完整性
     * @param chunkIndex 当前文件分片的索引（如果是分片上传）
     * @param chunks     文件的总分片数（如果是分片上传）
     * @return 上传结果，包含上传是否成功、上传后的文件路径等信息
     */
    UploadResultDto uploadFile(SessionWebUserDto webUserDto, String fileId,
                               MultipartFile file, String fileName,
                               String filePid, String fileMd5,
                               Integer chunkIndex, Integer chunks);
}

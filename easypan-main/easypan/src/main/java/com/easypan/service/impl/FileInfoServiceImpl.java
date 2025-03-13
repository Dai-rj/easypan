package com.easypan.service.impl;

import java.io.File;
import java.util.Date;
import java.util.List;

import com.easypan.component.RedisComponent;
import com.easypan.entity.config.AppConfig;
import com.easypan.entity.constants.Constants;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.dto.UploadResultDto;
import com.easypan.entity.dto.UserSpaceDto;
import com.easypan.entity.enums.*;
import com.easypan.entity.po.UserInfo;
import com.easypan.entity.query.UserInfoQuery;
import com.easypan.exception.BusinessException;
import com.easypan.mappers.UserInfoMapper;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.easypan.entity.query.FileInfoQuery;
import com.easypan.entity.po.FileInfo;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.entity.query.SimplePage;
import com.easypan.mappers.FileInfoMapper;
import com.easypan.service.FileInfoService;
import com.easypan.utils.StringTools;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


/**
 * 文件信息 业务接口实现
 */
@Service("fileInfoService")
public class FileInfoServiceImpl implements FileInfoService {
	private static final Logger logger = LoggerFactory.getLogger(FileInfoServiceImpl.class);
	@Resource
	private FileInfoMapper<FileInfo, FileInfoQuery> fileInfoMapper;
	@Resource
	private RedisComponent redisComponent;
	@Resource
	private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
	@Resource
	private AppConfig appConfig;
	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<FileInfo> findListByParam(FileInfoQuery param) {
		return fileInfoMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(FileInfoQuery param) {
		return fileInfoMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 * 该方法用于根据指定的查询参数分页查询文件信息列表
	 * 它首先计算满足查询条件的总记录数，然后根据分页参数获取当前页的记录列表
	 * 最后，将分页信息和记录列表封装到PaginationResultVO对象中返回
	 *
	 * @param param FileInfoQuery对象，包含查询条件和分页参数
	 * @return PaginationResultVO<FileInfo>对象，包含分页信息和文件信息列表
	 */
	@Override
	public PaginationResultVO<FileInfo> findListByPage(FileInfoQuery param) {
	    // 根据查询条件计算总记录数
	    int count = this.findCountByParam(param);
	    // 获取每页记录数，如果没有设置，则使用默认值
	    int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

	    // 创建SimplePage对象，用于存储分页信息
	    SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
	    param.setSimplePage(page);
	    // 根据查询条件和分页信息获取当前页的记录列表
	    List<FileInfo> list = this.findListByParam(param);
	    // 创建PaginationResultVO对象，用于存储分页查询结果
	    PaginationResultVO<FileInfo> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
	    return result;
	}

	/**
	 * 新增文件信息
	 *
	 * 该方法用于将一个FileInfo对象添加到数据库中它调用了fileInfoMapper的insert方法来执行实际的插入操作
	 *
	 * @param bean 要添加的FileInfo对象包含文件的相关信息
	 * @return 插入操作影响的行数通常为1表示成功，0表示失败
	 */
	@Override
	public Integer add(FileInfo bean) {
	    return fileInfoMapper.insert(bean);
	}

	/**
	 * 批量新增文件信息
	 *
	 * 该方法用于批量插入文件信息到数据库，以提高插入效率
	 * 主要用于处理多个文件信息的批量添加操作，减少数据库的访问次数
	 *
	 * @param listBean 文件信息列表，包含待插入的文件信息对象
	 *                 如果列表为空或null，则不执行任何操作，返回0
	 * @return 插入的记录数如果输入的列表为空或null，则返回0
	 */
	@Override
	public Integer addBatch(List<FileInfo> listBean) {
	    // 检查输入列表是否为空或null，如果为null或空列表，则不执行插入操作，返回0
	    if (listBean == null || listBean.isEmpty()) {
	        return 0;
	    }
	    // 调用Mapper层的批量插入方法，执行批量插入操作，并返回插入的记录数
	    return fileInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改文件信息
	 * 此方法用于处理一组FileInfo对象的批量插入或更新操作
	 * 如果传入的列表为空或为null，则不执行任何操作并返回0
	 * 否则，将调用mapper层的相应方法来执行批量插入或更新
	 *
	 * @param listBean 包含FileInfo对象的列表，用于批量插入或更新
	 *                 如果列表为空或为null，方法将不执行任何操作
	 * @return 返回受影响的行数，表示成功插入或更新的记录数
	 *         如果输入无效（null或空列表），则返回0
	 */
	@Override
	public Integer addOrUpdateBatch(List<FileInfo> listBean) {
	    // 检查输入列表是否为空或为null，如果 true，则不执行任何操作并返回0
	    if (listBean == null || listBean.isEmpty()) {
	        return 0;
	    }
	    // 调用mapper层方法，执行批量插入或更新操作，并返回受影响的行数
	    return fileInfoMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 *
	 * 此方法用于根据多个条件更新文件信息它接受一个FileInfo对象和一个FileInfoQuery对象作为参数
	 * FileInfo对象包含要更新的属性，而FileInfoQuery对象包含查询条件
	 *
	 * @param bean FileInfo对象，包含要更新的文件信息属性
	 * @param param FileInfoQuery对象，包含更新的查询条件
	 * @return 返回受影响的行数，表示更新操作的结果
	 */
	@Override
	public Integer updateByParam(FileInfo bean, FileInfoQuery param) {
	    // 检查查询参数的有效性
	    StringTools.checkParam(param);
	    // 调用Mapper层的updateByParam方法执行更新操作
	    return fileInfoMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 *
	 * 根据FileInfoQuery对象中的参数，删除符合条件的文件信息
	 * 此方法支持通过多个条件来筛选待删除的文件信息，提高了删除操作的灵活性和针对性
	 *
	 * @param param 包含删除条件的FileInfoQuery对象，用于指定删除操作的筛选条件
	 * @return 返回删除操作影响的行数，表示成功删除的文件信息数量
	 */
	@Override
	public Integer deleteByParam(FileInfoQuery param) {
	    // 检查参数有效性，确保传入的参数对象不为空，以避免后续操作因空指针而产生错误
	    StringTools.checkParam(param);
	    // 调用Mapper层的deleteByParam方法执行实际的删除操作，传入参数对象以传递删除条件
	    return fileInfoMapper.deleteByParam(param);
	}

	/**
	 * 根据FileIdAndUserId获取对象
	 *
	 * 该方法用于从数据库中获取特定用户特定文件的文件信息
	 * 它通过调用FileInfoMapper中的selectByFileIdAndUserId方法来实现
	 *
	 * @param fileId 文件ID，用于标识特定的文件
	 * @param userId 用户ID，用于标识特定的用户
	 * @return FileInfo对象，包含文件信息如果找不到匹配的文件信息，则返回null
	 */
	@Override
	public FileInfo getFileInfoByFileIdAndUserId(String fileId, String userId) {
	    return fileInfoMapper.selectByFileIdAndUserId(fileId, userId);
	}

	/**
	 * 根据FileIdAndUserId修改文件信息
	 *
	 * @param bean    要更新的文件信息对象
	 * @param fileId  文件ID，用于定位要更新的文件
	 * @param userId  用户ID，用于定位文件的所有者
	 * @return        返回更新影响的行数
	 *
	 * 此方法通过文件ID和用户ID来定位数据库中的特定文件信息，并应用更新操作
	 * 它依赖于fileInfoMapper中的updateByFileIdAndUserId方法来实现实际的数据库更新逻辑
	 */
	@Override
	public Integer updateFileInfoByFileIdAndUserId(FileInfo bean, String fileId, String userId) {
	    return fileInfoMapper.updateByFileIdAndUserId(bean, fileId, userId);
	}

	/**
	 * 根据FileIdAndUserId删除文件信息
	 *
	 * @param fileId 文件ID，用于标识待删除的文件
	 * @param userId 用户ID，用于标识执行删除操作的用户
	 * @return 返回删除操作的影响行数，通常为1表示成功，0表示失败或未找到对应记录
	 */
	@Override
	public Integer deleteFileInfoByFileIdAndUserId(String fileId, String userId) {
	    return fileInfoMapper.deleteByFileIdAndUserId(fileId, userId);
	}

	/**
	 * 上传文件方法
	 *
	 * @param webUserDto 当前用户信息
	 * @param fileId 文件ID
	 * @param file 要上传的文件
	 * @param fileName 文件名
	 * @param filePid 文件父ID
	 * @param fileMd5 文件MD5值
	 * @param chunkIndex 当前分块索引
	 * @param chunks 总分块数
	 * @return 上传结果DTO
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public UploadResultDto uploadFile(SessionWebUserDto webUserDto,
	                                  String fileId, MultipartFile file,
	                                  String fileName, String filePid,
	                                  String fileMd5, Integer chunkIndex,
	                                  Integer chunks) {
	    UploadResultDto resultDto = new UploadResultDto();
	    try {
	        // 如果文件ID为空，则生成一个随机文件ID
	        if (StringTools.isEmpty(fileId)) {
	            fileId = StringTools.getRandomString(Constants.LENGTH_10);
	        }
	        resultDto.setFileId(fileId);
	        Date curDate = new Date();
	        // 获取用户空间信息
	        UserSpaceDto spaceDto = redisComponent.getUserSpaceDto(webUserDto.getUserId());

	        // 处理文件分块上传的首个分块
	        if (chunkIndex == 0) {
	            // 查询是否有相同的文件MD5，用于秒传功能
	            FileInfoQuery infoQuery = new FileInfoQuery();
	            infoQuery.setFileMd5(fileMd5);
	            infoQuery.setSimplePage(new SimplePage(0, 1));
	            infoQuery.setStatus(FileStatusEnums.USING.getStatus());
	            List<FileInfo> dbFileList = fileInfoMapper.selectList(infoQuery);
	            // 秒传
	            if (!dbFileList.isEmpty()) {
	                FileInfo dbFile = dbFileList.get(0);
	                // 判断文件大小是否超过用户剩余空间
	                if (dbFile.getFileSize() + spaceDto.getUseSpace() > spaceDto.getTotalSpace()) {
	                    throw new BusinessException(ResponseCodeEnum.CODE_904);
	                }
	                dbFile.setFileId(fileId);
	                dbFile.setFilePid(filePid);
	                dbFile.setUserId(webUserDto.getUserId());
	                dbFile.setCreateTime(curDate);
	                dbFile.setLastUpdateTime(curDate);
	                dbFile.setStatus(FileStatusEnums.USING.getStatus());
	                dbFile.setDelFlag(FileDelFlagEnums.USING.getFlag());
	                dbFile.setFileMd5(fileMd5);
	                // 文件重命名
	                fileName = autoRename(filePid, webUserDto.getUserId(), fileName);
	                dbFile.setFileName(fileName);
	                fileInfoMapper.insert(dbFile);
	                resultDto.setStatus(UploadStatusEnums.UPLOAD_SECONDS.getCode());
	                // 更新用户使用空间
	                updateUserSpace(webUserDto, dbFile.getFileSize());
	                return resultDto;
	            }
	            // 判断磁盘空间是否足够
	            Long currentTempSize = redisComponent.getFileTempSize(webUserDto.getUserId(), fileId);
	            if (file.getSize() + currentTempSize + spaceDto.getUseSpace() > spaceDto.getTotalSpace()) {
	                throw new BusinessException(ResponseCodeEnum.CODE_904);
	            }
	            // 暂存临时目录
	            String tempFolderName = appConfig.getProjectFolder() + Constants.FILE_FOLDER_TEMP;
	            String currentUserFolderName = webUserDto.getUserId() + fileId;
	            File tempFileFolder = new File(tempFolderName + currentUserFolderName);
	            if (!tempFileFolder.exists()) {
	                tempFileFolder.mkdirs();
	            }

	            File newFile = new File(tempFileFolder.getPath() + "/" + chunkIndex);
	            file.transferTo(newFile);
	        }
	    } catch (Exception e) {
	        logger.error("文件上传失败", e);
	    }
	    return resultDto;
	}

	/**
	 * 自动重命名文件
	 * 当文件名已存在时，通过调用工具类的重命名方法来获取一个新的文件名
	 * 这是为了避免文件名重复，确保文件的唯一性
	 *
	 * @param filePid 文件的父目录ID，用于定位文件所在目录
	 * @param userId 用户ID，用于关联文件的拥有者
	 * @param fileName 原始文件名，需要检查是否已存在
	 * @return 如果文件名已存在，则返回新的文件名；否则返回原始文件名
	 */
	private String autoRename(String filePid, String userId, String fileName) {
	    // 创建文件信息查询对象，用于后续查询文件信息
	    FileInfoQuery fileInfoQuery = new FileInfoQuery();
	    // 设置查询条件：用户ID，以限定查询范围到当前用户
	    fileInfoQuery.setUserId(userId);
	    // 设置查询条件：文件的父目录ID，用于定位文件所在的具体目录
	    fileInfoQuery.setFilePid(filePid);
	    // 设置查询条件：删除标志为未删除状态，确保只查询当前使用的文件
	    fileInfoQuery.setDelFlag(FileDelFlagEnums.USING.getFlag());
	    // 设置查询条件：文件名，检查是否有相同的文件名已存在
	    fileInfoQuery.setFileName(fileName);
	    // 调用Mapper接口的查询方法，统计满足条件的文件数量
	    Integer count = fileInfoMapper.selectCount(fileInfoQuery);
	    // 如果查询结果表明存在相同的文件名
	    if (count > 0) {
	        // 则调用工具类的重命名方法，生成并返回新的文件名
	        fileName = StringTools.rename(fileName);
	    }
	    // 返回最终确定的文件名，无论是原始的还是重命名后的
	    return fileName;
	}

	/**
	 * 更新用户的使用空间
	 * 当更新失败时（即没有行受到影响），抛出业务异常
	 *
	 * @param webUserDto 包含用户信息的DTO
	 * @param useSpace 用户使用的空间量
	 * @throws BusinessException 当更新失败时抛出的业务异常
	 */
	private void updateUserSpace(SessionWebUserDto webUserDto, Long useSpace) {
	    // 更新数据库中的用户空间信息
	    Integer count = userInfoMapper.updateUserSpace(webUserDto.getUserId(), useSpace, null);
	    // 如果更新失败，则抛出异常
	    if (count == 0) {
	        throw new BusinessException(ResponseCodeEnum.CODE_904);
	    }
	    // 从Redis中获取用户的空间信息
	    UserSpaceDto spaceDto = redisComponent.getUserSpaceDto(webUserDto.getUserId());
	    // 更新用户的已使用空间
	    spaceDto.setUseSpace(spaceDto.getUseSpace() + useSpace);
	    // 将更新后的空间信息保存到Redis
	    redisComponent.saveUserSpaceUse(webUserDto.getUserId(), spaceDto);
	}
}
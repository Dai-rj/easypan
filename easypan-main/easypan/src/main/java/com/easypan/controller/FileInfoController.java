package com.easypan.controller;

import com.easypan.annotation.GlobalInterceptor;
import com.easypan.annotation.VerifyParam;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.dto.UploadResultDto;
import com.easypan.entity.enums.FileCategoryEnums;
import com.easypan.entity.enums.FileDelFlagEnums;
import com.easypan.entity.po.FileInfo;
import com.easypan.entity.query.FileInfoQuery;
import com.easypan.entity.vo.FileInfoVO;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.entity.vo.ResponseVO;
import com.easypan.service.FileInfoService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件信息 Controller
 */
@RestController("fileInfoController")
@RequestMapping("file")
public class FileInfoController extends ABaseController{

	@Resource
	private FileInfoService fileInfoService;
	/**
	 * 根据条件分页查询文件列表
	 * 该方法用于根据不同的条件和类别，分页查询文件信息
	 * 它首先根据提供的类别代码获取对应的枚举类型，然后设置查询参数，
	 * 包括用户ID、排序方式和删除标志，最后调用服务层方法执行查询
	 *
	 * @param session 用户会话，用于获取当前用户信息
	 * @param query 文件查询对象，包含查询条件和分页信息
	 * @param category 文件类别代码，用于确定查询的文件类别
	 * @return 返回一个包含查询结果的响应对象
	 */
	@RequestMapping("/loadDataList")
	@GlobalInterceptor
	public ResponseVO loadDataList(HttpSession session, FileInfoQuery query, String category) {
	    // 根据类别代码获取对应的文件类别枚举
	    FileCategoryEnums categoryEnum = FileCategoryEnums.getByCode(category);
	    // 如果找到了对应的枚举类型，则设置查询的文件类别
	    if (categoryEnum != null) {
	        query.setFileCategory(categoryEnum.getCategory());
	    }
	    // 设置查询的用户ID，从会话中获取当前用户信息
	    query.setUserId(getUserInfoFromSession(session).getUserId());
	    // 设置查询的排序方式，按最后更新时间降序排序
	    query.setOrderBy("last_update_time desc");
	    // 设置查询的删除标志，只查询未删除的文件
	    query.setDelFlag(FileDelFlagEnums.USING.getFlag());
	    // 调用服务层方法，根据查询条件分页查询文件列表
	    PaginationResultVO<FileInfo> result = fileInfoService.findListByPage(query);
	    // 将查询结果转换为分页VO，并返回成功的响应对象
	    return getSuccessResponseVO(convert2PaginationVO(result, FileInfoVO.class));
	}

	/**
	 * 处理文件上传请求
	 * 该方法使用了RequestMapping注解来映射/uploadFile URL的请求，以及GlobalInterceptor注解来进行参数校验
	 *
	 * @param session HttpSession对象，用于获取用户会话信息
	 * @param fileId 文件ID，用于标识文件
	 * @param file MultipartFile对象，表示上传的文件
	 * @param fileName 文件名，需要验证的参数，用于校验文件名称的合法性
	 * @param filePid 文件父ID，需要验证的参数，用于确定文件的所属关系
	 * @param fileMd5 文件的MD5值，需要验证的参数，用于校验文件的完整性
	 * @param chunkIndex 分片索引，需要验证的参数，用于处理大文件分片上传
	 * @param chunks 总分片数，需要验证的参数，用于确定文件分片的总数
	 * @return 返回一个ResponseVO对象，包含上传结果信息
	 */
	@RequestMapping("/uploadFile")
	@GlobalInterceptor(checkParams = true)
	public ResponseVO uploadFile(HttpSession session,
								 String fileId,
								 MultipartFile file,
								 // 需要验证的参数，用于校验文件名称的合法性
								 @VerifyParam(required = true) String fileName,
								 // 需要验证的参数，用于确定文件的所属关系
								 @VerifyParam(required = true) String filePid,
								 // 需要验证的参数，用于校验文件的完整性
								 @VerifyParam(required = true) String fileMd5,
								 // 需要验证的参数，用于处理大文件分片上传
								 @VerifyParam(required = true) Integer chunkIndex,
								 // 需要验证的参数，用于确定文件分片的总数
								 @VerifyParam(required = true) Integer chunks) {
		// 从会话中获取用户信息
		SessionWebUserDto webUserDto = getUserInfoFromSession(session);
		// 调用服务层方法处理文件上传逻辑
		UploadResultDto resultDto = fileInfoService.uploadFile(webUserDto, fileId, file, fileName, filePid, fileMd5, chunkIndex, chunks);
		// 构造并返回成功的响应对象
		return getSuccessResponseVO(resultDto);
	}
}
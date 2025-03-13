package com.easypan.controller;

import com.easypan.annotation.GlobalInterceptor;
import com.easypan.annotation.VerifyParam;
import com.easypan.component.RedisComponent;
import com.easypan.entity.config.AppConfig;
import com.easypan.entity.constants.Constants;
import com.easypan.entity.dto.CreateImageCode;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.dto.UserSpaceDto;
import com.easypan.entity.enums.VerifyRegexEnum;
import com.easypan.entity.po.UserInfo;
import com.easypan.entity.vo.ResponseVO;
import com.easypan.exception.BusinessException;
import com.easypan.service.EmailCodeService;
import com.easypan.service.UserInfoService;
import com.easypan.utils.StringTools;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import static org.apache.tomcat.util.http.fileupload.FileUploadBase.CONTENT_TYPE;

/**
 * 用户信息 Controller
 */
@RestController("userInfoController")
public class  AccountController extends ABaseController{

	private static final String CONTENT_TYPE_VALUE = "content-type-value";
	@Resource
	private UserInfoService userInfoService;
	@Resource
	private EmailCodeService emailCodeService;
    @Resource
    private AppConfig appConfig;
    @Resource
    private RedisComponent redisComponent;

	/**
	 * 生成和返回验证码图片
	 * 此方法根据类型生成不同用途的验证码，并将其保存到HTTP会话中，同时以图像形式返回
	 *
	 * @param response 用于向客户端返回验证码图片的HTTP响应对象
	 * @param session 用于存储验证码字符串的HTTP会话对象
	 * @param type 指定验证码的类型，null或0表示普通验证码，其他值表示邮箱验证码
	 * @throws IOException 如果在输出验证码图片时发生I/O错误
	 */
	@RequestMapping("/checkCode")
	public void checkCode(HttpServletResponse response, HttpSession session, Integer type) throws IOException {
	    // 创建验证码对象，参数分别为宽度、高度、字符数和干扰线数
	    CreateImageCode vCode = new CreateImageCode(130, 38, 5, 10);

	    // 设置响应头，确保浏览器不缓存此验证码图片
	    response.setHeader("Pragma", "no-cache");
	    response.setHeader("Cache-Control", "no-cache");
	    response.setDateHeader("Expires", 0);

	    // 设置响应内容类型为JPEG图像
	    response.setContentType("image/jpeg");

	    // 获取验证码字符串
	    String code = vCode.getCode();

	    // 根据类型将验证码字符串保存到会话中
	    if (type == null || type == 0) {
	        session.setAttribute(Constants.CHECK_CODE_KEY, code);
	    } else {
	        session.setAttribute(Constants.CHECK_CODE_KEY_EMAIL, code);
	    }

	    // 将验证码图片写入响应输出流
	    vCode.write(response.getOutputStream());
	}

	/**
	 * 发送邮箱验证码
	 *
	 * @param session HttpSession对象，用于获取和移除会话中的验证码
	 * @param email 用户输入的邮箱地址，经过正则验证确保格式正确
	 * @param checkCode 用户输入的图片验证码，用于验证用户身份
	 * @param type 邮箱验证码的类型，表示验证码的用途（如注册、找回密码等）
	 * @return ResponseVO对象，包含操作结果信息
	 *
	 * 此方法首先验证用户提交的图片验证码是否正确，如果正确则发送邮箱验证码，
	 * 否则抛出业务异常提示图片验证码不正确。发送验证码后，从会话中移除图片验证码信息。
	 */
	@RequestMapping("/sendEmailCode")
	public ResponseVO sendEmailCode(HttpSession session,
									@VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
									@VerifyParam(required = true) String checkCode,
									@VerifyParam(required = true) Integer type) {
		try {
			// 验证图片验证码是否正确
			// 从会话中获取图片验证码，并与用户输入的图片验证码进行比较
			if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY_EMAIL))) {
				// 如果图片验证码不正确，则抛出业务异常
				throw new BusinessException("图片验证码不正确");
			}
			// 发送邮箱验证码
			emailCodeService.sendEmailCode(email,type);
			// 返回成功响应
			return getSuccessResponseVO(null);
		} finally {
			// 无论是否成功发送验证码，都执行此操作
			// 从会话中移除图片验证码信息
			session.removeAttribute(Constants.CHECK_CODE_KEY_EMAIL);
		}
	}

	/**
	 * 处理用户注册请求
	 *
	 * @param session HTTP会话，用于获取和移除会话属性
	 * @param email 用户邮箱，需符合邮箱格式且长度不超过150
	 * @param nickName 用户昵称
	 * @param password 用户密码，需符合密码格式
	 * @param checkCode 图片验证码，用于验证用户身份
	 * @param emailCode 邮箱验证码，用于验证用户邮箱
	 * @return 返回注册结果的响应对象
	 */
	@RequestMapping("/register")
	@GlobalInterceptor(checkParams = true, checkLogin = false)
	public ResponseVO register(HttpSession session,
	                           @VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
	                           @VerifyParam(required = true) String nickName,
	                           @VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD) String password,
	                           @VerifyParam(required = true) String checkCode,
	                           @VerifyParam(required = true) String emailCode) {
	    try {
	        // 验证图片验证码是否正确
	        if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))) {
	            throw new BusinessException("图片验证码不正确");
	        }
	        // 调用服务层方法执行用户注册
	        userInfoService.register(email, nickName, password, emailCode);
	        // 返回成功响应
	        return getSuccessResponseVO(null);
	    } finally {
	        // 移除会话中的图片验证码
	        session.removeAttribute(Constants.CHECK_CODE_KEY);
	    }
	}

	/**
	 * 处理用户登录请求
	 *
	 * @param session HTTP会话，用于存储用户登录状态和验证码
	 * @param email 用户邮箱，用于用户身份验证
	 * @param password 用户密码，用于用户身份验证
	 * @param checkCode 用户输入的验证码，用于验证用户是否为机器人
	 * @return 返回一个包含登录结果的ResponseVO对象
	 */
	@RequestMapping("/login")
	@GlobalInterceptor(checkParams = true, checkLogin = false)
	public ResponseVO login(HttpSession session,
	                        @VerifyParam(required = true) String email,
	                        @VerifyParam(required = true) String password,
	                        @VerifyParam(required = true) String checkCode) {
	    try {
	        // 验证图片验证码是否正确，如果不正确则抛出异常
	        if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))) {
	            throw new BusinessException("图片验证码不正确");
	        }
	        // 调用用户信息服务进行登录，如果登录成功则将用户信息保存到会话中
	        SessionWebUserDto sessionWebUserDto = userInfoService.login(email, password);
	        session.setAttribute(Constants.SESSION_KEY, sessionWebUserDto);
	        // 返回成功响应，包含用户信息
	        return getSuccessResponseVO(sessionWebUserDto);
	    } finally {
	        // 登录完成后移除会话中的验证码，确保安全性
	        session.removeAttribute(Constants.CHECK_CODE_KEY);
	    }
	}
	/**
	 * 处理密码重置请求
	 *
	 * 本方法用于接收用户提交的重置密码请求，验证用户提供的信息，并在验证通过后更新用户的密码
	 * 使用了请求映射和全局拦截器注解，以指定请求路径和预处理逻辑
	 *
	 * @param session HTTP会话，用于访问会话级别的数据，如验证码
	 * @param email 用户邮箱，用于验证用户身份和发送重置邮件
	 * @param password 新密码，用户希望设置的新密码
	 * @param checkCode 图片验证码，用于验证用户是合法操作
	 * @param emailCode 邮箱验证码，用于验证邮箱所有者身份
	 * @return 返回一个响应对象，指示操作是否成功
	 *
	 * 注意：本方法中使用了参数验证注解，以确保输入数据的格式和安全性
	 */
	@RequestMapping("/resetPwd")
	@GlobalInterceptor(checkParams = true, checkLogin = false)
	public ResponseVO resetPwd(HttpSession session,
	                           @VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
	                           @VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD) String password,
	                           @VerifyParam(required = true) String checkCode,
	                           @VerifyParam(required = true) String emailCode) {
	    try {
	        // 验证图片验证码的正确性，以防止自动化脚本攻击
	        if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))) {
	            throw new BusinessException("图片验证码不正确");
	        }
	        // 调用服务层方法重置用户密码
	        userInfoService.resetPwd(email, password, emailCode);
	        // 返回成功响应
	        return getSuccessResponseVO(null);
	    } finally {
	        // 清除会话中的验证码，确保每个验证码只能使用一次
	        session.removeAttribute(Constants.CHECK_CODE_KEY);
	    }
	}

	/**
	 * 根据用户ID获取用户头像
	 * 如果头像不存在，则返回默认头像
	 *
	 * @param response HTTP响应对象，用于输出头像数据
	 * @param userId 用户ID，用于定位特定用户的头像
	 */
	@RequestMapping("/getAvatar/{userId}")
	@GlobalInterceptor(checkParams = true, checkLogin = false)
	public void getAvatar(HttpServletResponse response, @VerifyParam(required = true) @PathVariable("userId") String userId) {
	    // 组合头像文件夹路径
	    String avatarFolderName = Constants.FILE_FOLDER_FILE + Constants.FILE_FOLDER_AVATAR_NAME;
	    File folder = new File(appConfig.getProjectFolder() + avatarFolderName);
	    // 如果头像文件夹不存在，则创建它
	    if (!folder.exists()) {
	        folder.mkdirs();
	    }
	    // 组合头像文件路径
	    String avatarPath = appConfig.getProjectFolder() + avatarFolderName + userId + Constants.AVATAR_SUFFIX;
	    File file = new File(avatarPath);
	    // 如果头像文件不存在，则检查默认头像是否存在
	    if (!file.exists()) {
	        // 如果默认头像也不存在，则调用方法输出无默认头像的信息
	        if (!new File(appConfig.getProjectFolder() + avatarFolderName + Constants.AVATAR_DEFAULT).exists()) {
	            printNoDefaultImage(response);
	        }
	        // 将头像路径设置为默认头像路径
	        avatarPath = appConfig.getProjectFolder() + avatarFolderName + Constants.AVATAR_DEFAULT;
	    }
	    // 设置响应内容类型为JPEG图像
	    response.setContentType("image/jpg");
	    // 调用方法读取并输出头像文件
	    readFile(response, avatarPath);
	}

	/**
	 * 当没有默认头像时，向客户端输出信息
	 * 此方法在用户没有设置头像时被调用，以通知客户端应该在头像目录下放置一个默认头像
	 *
	 * @param response 用于向客户端发送响应的HttpServletResponse对象
	 */
	private void printNoDefaultImage(HttpServletResponse response) {
	    // 设置响应的内容类型和状态码
	    response.setHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE);
	    response.setStatus(HttpStatus.OK.value());

	    // 尝试获取响应的输出流，并输出提示信息
	    try (PrintWriter writer = response.getWriter()) {
	        writer.print("请在头像目录下放置默认头像default_avatar.jpg");
	    } catch (Exception e) {
	        // 如果输出过程中发生异常，记录错误日志
	        logger.error("输出无默认图失败", e);
	    }
	}

//	@RequestMapping("/getUserInfo")
//	@GlobalInterceptor(checkParams = true)
//	public ResponseVO getUserInfo(HttpSession session) {
//		SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
//		return getSuccessResponseVO(sessionWebUserDto);
//	}

	/**
	 * 获取用户使用空间信息
	 *
	 * @param session HTTP会话，用于获取用户信息
	 * @return 返回包含用户使用空间信息的响应对象
	 */
	@RequestMapping("/getUseSpace")
	@GlobalInterceptor
	public ResponseVO getUseSpace(HttpSession session) {
	    // 从会话中获取用户信息
	    SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
	    // 通过Redis组件获取用户空间使用情况
	    UserSpaceDto userSpaceDto = redisComponent.getUserSpaceDto(sessionWebUserDto.getUserId());
	    // 返回成功响应，包含用户空间使用信息
	    return getSuccessResponseVO(userSpaceDto);
	}

	/**
	 * 处理用户登出请求
	 * 通过使当前会话无效来实现用户登出
	 *
	 * @param session 用户的会话对象，用于管理用户会话状态
	 * @return 返回一个表示操作成功的ResponseVO对象，不含额外数据
	 */
	@RequestMapping("/logout")
	public ResponseVO logout(HttpSession session) {
	    // 使当前会话无效，以实现用户登出
	    session.invalidate();
	    // 返回一个成功的响应对象，不含任何附加数据
	    return getSuccessResponseVO(null);
	}

	/**
	 * 更新用户头像
	 *
	 * @param session 用户会话，用于获取当前登录用户信息
	 * @param avatar 用户上传的头像文件
	 * @return 返回更新头像后的响应信息
	 */
	@RequestMapping("/updateUserAvatar")
	@GlobalInterceptor
	public ResponseVO updateUserAvatar(HttpSession session, MultipartFile avatar) {
	    // 从会话中获取用户信息
	    SessionWebUserDto webUserDto = getUserInfoFromSession(session);
	    // 获取项目文件夹路径，并拼接上文件存储的子目录
	    String baseFolder = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE;
	    // 指定头像文件的保存目录
	    File targetFileFolder = new File(baseFolder + Constants.FILE_FOLDER_AVATAR_NAME);
	    // 如果目标目录不存在，则创建该目录
	    if (!targetFileFolder.exists()) {
	        targetFileFolder.mkdirs();
	    }
	    // 构造头像文件的完整路径，包括用户ID和头像文件后缀
	    File targetFile = new File(targetFileFolder.getPath() + "/" + webUserDto.getUserId() + Constants.AVATAR_SUFFIX);
	    try {
	        // 将上传的头像文件转移到目标文件中
	        avatar.transferTo(targetFile);
	    } catch (Exception e) {
	        // 日志记录上传头像过程中出现的错误
	        logger.error("上传头像失败", e);
	    }
	    // 初始化用户信息对象，清空QQ头像URL，以支持新的头像上传
	    UserInfo userInfo = new UserInfo();
	    userInfo.setQqAvatar("");
	    // 更新数据库中的用户信息
	    userInfoService.updateUserInfoByUserId(userInfo, webUserDto.getUserId());
	    // 更新会话中的用户头像信息为null，表示头像已更新
	    webUserDto.setAvatar(null);
	    session.setAttribute(Constants.SESSION_KEY, webUserDto);
	    // 返回成功响应
	    return getSuccessResponseVO(null);
	}

	/**
	 * 更新用户密码的请求映射
	 * 该方法使用全局拦截器检查参数，并验证密码格式
	 *
	 * @param session HTTP会话，用于获取用户信息
	 * @param password 新密码，经过验证后用于更新用户信息
	 * @return 返回操作结果的响应对象
	 */
	@RequestMapping("/updatePassword")
	@GlobalInterceptor(checkParams = true)
	public ResponseVO updatePassword(HttpSession session,
									 @VerifyParam(required = true,
											 regex = VerifyRegexEnum.PASSWORD,
											 min = 8, max = 18) String password) {
		// 从会话中获取用户信息
		SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);

		// 创建用户信息对象，用于更新密码
		UserInfo userInfo = new UserInfo();
		// 使用MD5对密码进行加密
		userInfo.setPassword(StringTools.encodeByMd5(password));

		// 调用服务层方法更新用户信息
		userInfoService.updateUserInfoByUserId(userInfo, sessionWebUserDto.getUserId());

		// 返回成功响应
		return getSuccessResponseVO(null);
	}
}
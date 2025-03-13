package com.easypan.service.impl;

import java.util.Date;
import java.util.List;

import com.easypan.component.RedisComponent;
import com.easypan.entity.config.AppConfig;
import com.easypan.entity.constants.Constants;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.dto.SysSettingsDto;
import com.easypan.entity.dto.UserSpaceDto;
import com.easypan.entity.enums.UserStatusEnum;
import com.easypan.entity.po.FileInfo;
import com.easypan.entity.query.FileInfoQuery;
import com.easypan.exception.BusinessException;
import com.easypan.mappers.FileInfoMapper;
import com.easypan.service.EmailCodeService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;

import com.easypan.entity.enums.PageSize;
import com.easypan.entity.query.UserInfoQuery;
import com.easypan.entity.po.UserInfo;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.entity.query.SimplePage;
import com.easypan.mappers.UserInfoMapper;
import com.easypan.service.UserInfoService;
import com.easypan.utils.StringTools;
import org.springframework.transaction.annotation.Transactional;


/**
 * 用户信息 业务接口实现
 */
@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {

	@Resource
	private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
	@Resource
	private FileInfoMapper<FileInfo, FileInfoQuery> fileInfoMapper;
	@Resource
	private EmailCodeService emailCodeService;
	@Resource
	private RedisComponent redisComponent;
	@Resource
	private AppConfig appConfig;
	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<UserInfo> findListByParam(UserInfoQuery param) {
		// 调用userInfoMapper的selectList方法，传入查询参数param，返回查询结果列表
		return userInfoMapper.selectList(param);
	}


	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(UserInfoQuery param) {
		// 调用userInfoMapper的selectCount方法，传入查询参数param，返回查询结果的数量
		return userInfoMapper.selectCount(param);
	}


	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<UserInfo> findListByPage(UserInfoQuery param) {
		// 查询总记录数
		int count = this.findCountByParam(param);
		// 获取分页大小，默认为PageSize.SIZE15
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		// 创建分页对象
		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		// 将分页对象设置到查询参数中
		param.setSimplePage(page);
		// 根据查询参数获取分页数据列表
		List<UserInfo> list = this.findListByParam(param);
		// 封装分页结果并返回
		return new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
	}


	/**
	 * 新增
	 */
	@Override
	// 实现新增方法
	public Integer add(UserInfo bean) {
		// 调用userInfoMapper的insert方法，将UserInfo对象bean插入数据库
		return userInfoMapper.insert(bean);
	}


	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<UserInfo> listBean) {
		// 判断传入的List是否为空或者长度为0
		if (listBean == null || listBean.isEmpty()) {
			// 如果为空或长度为0，则返回0
			return 0;
		}
		// 调用userInfoMapper的insertBatch方法，将用户信息列表批量插入数据库
		return userInfoMapper.insertBatch(listBean);
	}


	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<UserInfo> listBean) {
		// 检查传入的列表是否为空或者长度为0
		if (listBean == null || listBean.isEmpty()) {
			// 如果为空或长度为0，则返回0
			return 0;
		}
		// 调用userInfoMapper的insertOrUpdateBatch方法，执行批量新增或修改操作
		return this.userInfoMapper.insertOrUpdateBatch(listBean);
	}


	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(UserInfo bean, UserInfoQuery param) {
		// 校验参数
		StringTools.checkParam(param);
		// 调用userInfoMapper的updateByParam方法执行更新操作
		return userInfoMapper.updateByParam(bean, param);
	}


	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(UserInfoQuery param) {
		// 校验参数
		StringTools.checkParam(param);
		// 调用userInfoMapper的deleteByParam方法执行删除操作
		return userInfoMapper.deleteByParam(param);
	}


	/**
	 * 根据UserId获取对象
	 */
	@Override
	public UserInfo getUserInfoByUserId(String userId) {
		// 调用userInfoMapper的selectByUserId方法，传入userId参数
		// 返回查询结果，即对应的UserInfo对象
		return userInfoMapper.selectByUserId(userId);
	}


	/**
	 * 根据UserId修改
	 */
	@Override
	public Integer updateUserInfoByUserId(UserInfo bean, String userId) {
		return userInfoMapper.updateByUserId(bean, userId);
	}

	/**
	 * 根据UserId删除
	 */
	@Override
	public Integer deleteUserInfoByUserId(String userId) {
		return userInfoMapper.deleteByUserId(userId);
	}

	/**
	 * 根据Email获取对象
	 */
	@Override
	public UserInfo getUserInfoByEmail(String email) {
		return userInfoMapper.selectByEmail(email);
	}

	/**
	 * 根据Email修改
	 */
	@Override
	public Integer updateUserInfoByEmail(UserInfo bean, String email) {
		return userInfoMapper.updateByEmail(bean, email);
	}

	/**
	 * 根据Email删除
	 */
	@Override
	public Integer deleteUserInfoByEmail(String email) {
		return userInfoMapper.deleteByEmail(email);
	}

	/**
	 * 根据QqOpenId获取对象
	 */
	@Override
	public UserInfo getUserInfoByQqOpenId(String qqOpenId) {
		return userInfoMapper.selectByQqOpenId(qqOpenId);
	}

	/**
	 * 根据QqOpenId修改
	 */
	@Override
	public Integer updateUserInfoByQqOpenId(UserInfo bean, String qqOpenId) {
		return userInfoMapper.updateByQqOpenId(bean, qqOpenId);
	}

	/**
	 * 根据QqOpenId删除
	 */
	@Override
	public Integer deleteUserInfoByQqOpenId(String qqOpenId) {
		return userInfoMapper.deleteByQqOpenId(qqOpenId);
	}

	/**
	 * 根据NickName获取对象
	 */
	@Override
	public UserInfo getUserInfoByNickName(String nickName) {
		return userInfoMapper.selectByNickName(nickName);
	}

	/**
	 * 根据NickName修改
	 */
	@Override
	public Integer updateUserInfoByNickName(UserInfo bean, String nickName) {
		return userInfoMapper.updateByNickName(bean, nickName);
	}

	/**
	 * 根据NickName删除
	 */
	@Override
	public Integer deleteUserInfoByNickName(String nickName) {
		return this.userInfoMapper.deleteByNickName(nickName);
	}

	/**
	 * 注册新用户
	 *
	 * @param email 用户邮箱，作为登录账号
	 * @param nickName 用户昵称，用于展示
	 * @param password 用户密码，用于登录验证
	 * @param emailCode 邮箱验证码，用于验证邮箱有效性
	 *
	 * 此方法用于处理用户注册请求，包括验证邮箱和昵称的唯一性、验证邮箱验证码的正确性，
	 * 以及创建并保存新的用户信息到数据库中如果在注册过程中发生任何异常，
	 * 事务将回滚以保证数据一致性
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void register(String email, String nickName, String password, String emailCode) {
	    // 通过邮箱查询用户信息
	    UserInfo userInfo = userInfoMapper.selectByEmail(email);
	    // 如果用户信息不为空，则抛出邮箱账号已经存在的异常
	    if (userInfo != null) {
	        throw new BusinessException("邮箱账号已经存在");
	    }

	    // 通过昵称查询用户信息
	    UserInfo nickNameUser = this.userInfoMapper.selectByNickName(nickName);
	    // 如果昵称用户信息不为空，则抛出昵称已经存在的异常
	    if (nickNameUser != null) {
	        throw new BusinessException("昵称已经存在");
	    }

	    // 校验邮箱验证码
	    emailCodeService.checkCode(email, emailCode);

	    // 生成随机用户ID
	    String userId = StringTools.getRandomNumber(Constants.LENGTH_10);
	    // 创建新的UserInfo对象
	    userInfo = new UserInfo();
	    // 设置用户ID
	    userInfo.setUserId(userId);
	    // 设置用户昵称
	    userInfo.setNickName(nickName);
	    // 设置用户邮箱
	    userInfo.setEmail(email);
	    // 对密码进行MD5加密后设置
	    userInfo.setPassword(StringTools.encodeByMd5(password));
	    // 设置用户加入时间
	    userInfo.setJoinTime(new Date());
	    // 设置用户状态为启用
	    userInfo.setStatus(UserStatusEnum.ENABLE.getStatus());
	    // 设置用户已使用空间为0
	    userInfo.setUseSpace(0L);
	    // 从Redis中获取系统设置信息
	    SysSettingsDto sysSettingsDto = redisComponent.getSysSettingsDto();
	    // 设置用户初始总空间（单位转换为MB）
	    userInfo.setTotalSpace(sysSettingsDto.getUserInitUseSpace() * Constants.MB);
	    // 将用户信息插入数据库
	    userInfoMapper.insert(userInfo);
	}


	/**
	 * 用户登录方法
	 *
	 * @param email 用户邮箱
	 * @param password 用户密码
	 * @return 登录成功后返回会话信息对象，包含用户昵称、用户ID、管理员权限等信息
	 * @throws BusinessException 如果用户账号或密码错误，或者账号已被禁用，则抛出业务异常
	 */
	@Override
	public SessionWebUserDto login(String email, String password) {
		// 通过邮箱查询用户信息
		UserInfo userInfo = userInfoMapper.selectByEmail(email);
		// 如果用户信息为空或密码不匹配，则抛出账号或密码错误的业务异常
		if (userInfo == null || !userInfo.getPassword().equals(password)) {
			throw new BusinessException("账号或密码错误");
		}
		// 如果用户账号被禁用，则抛出账号已禁用的业务异常
		if (UserStatusEnum.DISABLE.getStatus().equals(userInfo.getStatus())) {
			throw new BusinessException("账号已禁用");
		}
		// 创建用于更新用户信息的UserInfo对象
		UserInfo updateInfo = new UserInfo();
		// 设置用户最后登录时间为当前时间
		updateInfo.setLastLoginTime(new Date());

		// 更新用户最后登录时间
		userInfoMapper.updateByUserId(updateInfo, userInfo.getUserId());

		// 创建SessionWebUserDto对象用于存储会话信息
		SessionWebUserDto sessionWebUserDto = new SessionWebUserDto();
		// 设置用户昵称
		sessionWebUserDto.setNickName(userInfo.getNickName());
		// 设置用户ID
		sessionWebUserDto.setUserId(userInfo.getUserId());
		// 判断用户邮箱是否在管理员邮箱列表中，设置管理员权限
        sessionWebUserDto.setIsAdmin(ArrayUtils.contains(appConfig.getAdminEmails().split(","), email));
		// 用户空间处理
		// 创建UserSpaceDto对象用于存储用户空间信息
		UserSpaceDto userSpaceDto = new UserSpaceDto();
		// 查询用户已使用空间大小
		Long useSpace = fileInfoMapper.selectUseSpace(userInfo.getUserId());
		// 设置用户已使用空间大小
		userSpaceDto.setUseSpace(useSpace);
		// 设置用户总空间大小
		userSpaceDto.setTotalSpace(userInfo.getTotalSpace());
		// 将用户空间信息保存到Redis中
		redisComponent.saveUserSpaceUse(userInfo.getUserId(), userSpaceDto);
		// 返回会话信息
		return sessionWebUserDto;
	}


	/**
	 * 重置用户密码
	 *
	 * @param email       用户邮箱
	 * @param password    新密码
	 * @param emailCode   邮箱验证码
	 * @throws BusinessException 如果邮箱账号不存在或邮箱验证码校验失败，将抛出业务异常
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void resetPwd(String email, String password, String emailCode) {
		// 通过邮箱查询用户信息
		UserInfo userInfo = userInfoMapper.selectByEmail(email);
		// 如果用户信息为空，则邮箱账号不存在
		if (userInfo == null) {
			// 抛出业务异常，提示邮箱账号不存在
			throw new BusinessException("邮箱账号不存在");
		}
		// 校验邮箱验证码
		emailCodeService.checkCode(email, emailCode);
		// 创建更新信息的UserInfo对象
		UserInfo updateInfo = new UserInfo();
		// 对密码进行MD5加密后设置到更新信息中
		updateInfo.setPassword(StringTools.encodeByMd5(password));
		// 通过邮箱更新用户信息
		userInfoMapper.updateByEmail(updateInfo, email);
	}
}
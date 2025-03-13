package com.easypan.service.impl;

import com.easypan.component.RedisComponent;
import com.easypan.entity.config.AppConfig;
import com.easypan.entity.constants.Constants;
import com.easypan.entity.dto.SysSettingsDto;
import com.easypan.entity.enums.PageSize;
import com.easypan.entity.po.EmailCode;
import com.easypan.entity.po.UserInfo;
import com.easypan.entity.query.EmailCodeQuery;
import com.easypan.entity.query.SimplePage;
import com.easypan.entity.query.UserInfoQuery;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.exception.BusinessException;
import com.easypan.mappers.EmailCodeMapper;
import com.easypan.mappers.UserInfoMapper;
import com.easypan.service.EmailCodeService;
import com.easypan.utils.StringTools;
import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 邮箱验证码 业务接口实现
 */
@Service("emailCodeService")
public class EmailCodeServiceImpl implements EmailCodeService {
    // 日志对象，用于记录日志信息
    private static final Logger logger = LoggerFactory.getLogger(EmailCodeServiceImpl.class);

    // 邮箱验证码数据访问对象，用于操作邮箱验证码相关数据
    @Resource
    private EmailCodeMapper<EmailCode, EmailCodeQuery> emailCodeMapper;
    // 用户信息数据访问对象，用于操作用户信息相关数据
    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
    // 邮件发送器，用于发送邮件
    @Resource
    private JavaMailSender javaMailSender;
    // 应用配置对象，用于获取应用配置信息
    @Resource
    private AppConfig appConfig;
    // Redis组件，用于操作Redis中的数据
    @Resource
    private RedisComponent redisComponent;

    /**
     * 根据条件查询列表
     *
     * @param param 查询条件
     * @return 查询结果列表
     */
    @Override
    public List<EmailCode> findListByParam(EmailCodeQuery param) {
        // 使用查询条件从数据库中选择符合条件的邮箱验证码记录
        return emailCodeMapper.selectList(param);
    }

    /**
     * 根据条件查询列表
     *
     * @param param 查询条件
     * @return 查询结果数量
     */
    @Override
    public Integer findCountByParam(EmailCodeQuery param) {
        // 使用查询条件param来调用emailCodeMapper的selectCount方法，返回查询到的记录数
        return emailCodeMapper.selectCount(param);
    }

    /**
     * 分页查询方法
     *
     * @param param 分页查询条件
     * @return 分页查询结果
     */
    @Override
    public PaginationResultVO<EmailCode> findListByPage(EmailCodeQuery param) {
        // 查询符合条件的总记录数
        int count = this.findCountByParam(param);
        // 获取每页大小，如果没有设置则使用默认值
        int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

        // 创建分页对象并设置分页参数
        SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
        param.setSimplePage(page);
        // 根据分页参数查询列表数据
        List<EmailCode> list = this.findListByParam(param);
        // 创建分页结果对象并设置分页信息和数据列表
        PaginationResultVO<EmailCode> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * 新增邮箱验证码记录
     *
     * @param bean 新增的邮箱验证码对象，包含验证码及相关信息
     * @return 新增结果，返回影响的行数，通常为1表示成功，0表示失败
     */
    @Override
    public Integer add(EmailCode bean) {
        // 调用Mapper接口的insert方法，将邮箱验证码对象插入数据库
        return emailCodeMapper.insert(bean);
    }

    /**
     * 批量新增邮箱验证码
     *
     * @param listBean 新增的邮箱验证码对象列表
     * @return 新增结果，成功返回影响的行数，失败返回0
     */
    @Override
    public Integer addBatch(List<EmailCode> listBean) {
        // 检查传入的列表是否为空或为空列表，如果为空，则返回0
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        // 调用Mapper层的批量插入方法，传入邮箱验证码列表，并返回插入结果
        return emailCodeMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或者修改邮箱验证码信息
     *
     * 此方法用于批量处理邮箱验证码的新增或修改操作它接收一个邮箱验证码对象列表，
     * 对列表中的每个对象进行插入操作如果对象已存在，则进行更新操作此方法在处理前会检查
     * 输入列表是否为空或null，如果检查不通过，则直接返回0，表示操作失败
     *
     * @param listBean 新增或修改的邮箱验证码对象列表，不能为空或null
     * @return 新增或修改结果返回受影响的行数，如果输入无效则返回0
     */
    @Override
    public Integer addOrUpdateBatch(List<EmailCode> listBean) {
        // 检查输入列表是否为空或null，如果检查不通过，则直接返回0
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        // 调用Mapper层的批量插入或更新方法，处理邮箱验证码列表
        return emailCodeMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 多条件更新邮箱验证码信息
     *
     * 此方法根据提供的更新条件参数，对邮箱验证码对象进行多条件更新操作
     * 它首先验证更新条件参数的有效性，然后调用emailCodeMapper的updateByParam方法执行实际的更新操作
     *
     * @param bean 包含要更新信息的邮箱验证码对象
     * @param param 指定更新的条件参数
     * @return 返回更新操作的结果，通常是一个表示受影响行数的整数
     */
    @Override
    public Integer updateByParam(EmailCode bean, EmailCodeQuery param) {
        // 验证更新条件参数的有效性
        StringTools.checkParam(param);
        // 调用emailCodeMapper的updateByParam方法执行实际的更新操作
        return emailCodeMapper.updateByParam(bean, param);
    }

    /**
     * 多条件删除
     *
     * @param param 删除条件
     * @return 删除结果
     */
    @Override
    public Integer deleteByParam(EmailCodeQuery param) {
        // 检查参数有效性
        StringTools.checkParam(param);
        // 调用Mapper方法进行条件删除
        return emailCodeMapper.deleteByParam(param);
    }

    /**
     * 根据EmailAndCode获取对象
     *
     * @param email 邮箱
     * @param code 验证码
     * @return 邮箱验证码对象
     */
    @Override
    public EmailCode getEmailCodeByEmailAndCode(String email, String code) {
        // 通过调用emailCodeMapper的selectByEmailAndCode方法，根据邮箱和验证码查询对应的邮箱验证码对象
        return emailCodeMapper.selectByEmailAndCode(email, code);
    }

    /**
     * 根据EmailAndCode修改
     *
     * @param bean 修改的邮箱验证码对象
     * @param email 邮箱
     * @param code 验证码
     * @return 修改结果
     */
    @Override
    public Integer updateEmailCodeByEmailAndCode(EmailCode bean, String email, String code) {
        return emailCodeMapper.updateByEmailAndCode(bean, email, code);
    }

    /**
     * 根据EmailAndCode删除
     *
     * @param email 邮箱
     * @param code 验证码
     * @return 删除结果
     */
    @Override
    public Integer deleteEmailCodeByEmailAndCode(String email, String code) {
        // 调用emailCodeMapper的deleteByEmailAndCode方法进行删除操作
        return emailCodeMapper.deleteByEmailAndCode(email, code);
    }

    /**
     * 发送邮箱验证码
     *
     * @param email 注册邮箱
     * @param type 操作类型
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendEmailCode(String email, Integer type) {
        // 如果是注册，找邮箱是否已经注册
        if (Objects.equals(type, Constants.ZERO)) {
            UserInfo userInfo = userInfoMapper.selectByEmail(email);
            if (userInfo != null) {
                throw new BusinessException("邮箱已存在");
            }
        }
        // 生成5位随机数字作为验证码
        String code = StringTools.getRandomNumber(Constants.LENGTH_5);
        // 发送验证码
        sendMailCode(email, code);
        // 将之前的验证码置为无效
        emailCodeMapper.disableEmailCode(email);

        // 创建新的邮箱验证码对象
        EmailCode emailCode = new EmailCode();
        emailCode.setCode(code);
        emailCode.setEmail(email);
        emailCode.setStatus(Constants.ZERO);
        emailCode.setCreateTime(new Date());
        // 插入新的邮箱验证码记录
        emailCodeMapper.insert(emailCode);
    }

    /**
     * 发送验证码具体实现
     *
     * @param toEmail 发送的邮箱
     * @param code 发送的验证码
     */
    private void sendMailCode(String toEmail, String code) {
        try {
            // 创建Mime邮件对象
            MimeMessage message = javaMailSender.createMimeMessage();
            // 初始化邮件帮助器，设置为true表示邮件内容为HTML格式
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // 设置发件人邮箱地址
            helper.setFrom(appConfig.getSendUserName());
            // 设置收件人邮箱地址
            helper.setTo(toEmail);

            // 从Redis中获取系统设置信息
            SysSettingsDto sysSettingsDto = redisComponent.getSysSettingsDto();
            // 设置邮件主题
            helper.setSubject(sysSettingsDto.getRegisterMailTitle());
            // 设置邮件内容，使用系统设置中的内容格式，并插入验证码
            helper.setText(String.format(sysSettingsDto.getRegisterEmailContent(), code));

            // 设置邮件发送日期为当前日期
            helper.setSentDate(new Date());

            // 发送邮件
            javaMailSender.send(message);
        } catch (Exception e) {
            // 记录邮件发送失败的日志
            logger.error("邮件发送失败");
            // 抛出业务异常，提示邮件发送失败
            throw new BusinessException("邮件发送失败");
        }
    }

    /**
     * 验证邮箱验证码是否有效
     *
     * @param email 用户提供的邮箱地址，用于查找对应的验证码
     * @param code 用户输入的验证码，用于与系统记录的验证码进行匹配
     * @throws BusinessException 当验证码不正确或已失效时抛出的异常
     */
    @Override
    public void checkCode(String email, String code) {
        // 根据邮箱和验证码查询数据库中的验证码记录
        EmailCode emailCode = emailCodeMapper.selectByEmailAndCode(email, code);

        // 检查查询结果是否为空，如果为空，则抛出异常提示验证码不正确
        if (emailCode == null) {
            throw new BusinessException("邮箱验证码不正确");
        }

        // 检查验证码的状态是否为已使用，或是否超过了15分钟的有效期，如果满足条件之一，则抛出异常提示验证码已失效
        if (emailCode.getStatus() == 1 ||
                System.currentTimeMillis() - emailCode.getCreateTime().getTime() > Constants.LENGTH_15 * 1000 * 60) {
            throw new BusinessException("邮箱验证码已失效");
        }

        // 验证码验证通过后，调用方法将该验证码标记为已使用，确保其不能再被使用
        emailCodeMapper.disableEmailCode(email);
    }
}

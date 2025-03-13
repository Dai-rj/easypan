package com.easypan.aspect;

import com.easypan.annotation.GlobalInterceptor;
import com.easypan.annotation.VerifyParam;
import com.easypan.entity.constants.Constants;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.enums.ResponseCodeEnum;
import com.easypan.exception.BusinessException;
import com.easypan.utils.StringTools;
import com.easypan.utils.VerifyUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;

@Aspect
@Component("globalOperationAspect")
public class GlobalOperationAspect {

    private static final Logger logger = LoggerFactory.getLogger(GlobalOperationAspect.class);
    private static final String[] TYPE_BASE = {"java.lang.String", "java.lang.Integer", "java.lang.Long"};

    /**
     * 切入点方法，用于拦截带有特定注解的请求
     * 该方法本身无具体实现，主要作为切点表达式的载体
     * <p>
     * &#064;Pointcut  注解用于定义一个切点，此处切点规则为：
     *           "@annotation(com.easypan.annotation.GlobalInterceptor)"，表示拦截所有使用了
     *           com.easypan.annotation.GlobalInterceptor注解的方法
     */
    @Pointcut("@annotation(com.easypan.annotation.GlobalInterceptor)")
    private void requestInterceptor() {
        // 此处不需要添加任何代码，因为该方法的目的是定义一个切点
    }

    /**
     * 在执行请求拦截器之前进行的操作
     * 该方法主要用于处理全局拦截器逻辑，包括登录验证、权限验证和参数校验
     *
     * @param point 切入点对象，提供了关于当前执行方法的信息
     * @throws BusinessException 当验证失败或出现业务异常时抛出
     */
    @Before("requestInterceptor()")
    public void interceptorDo(JoinPoint point) throws BusinessException {
        try {
            // 获取目标对象（执行被拦截方法的对象）
            Object target = point.getTarget();
            // 获取方法参数
            Object[] args = point.getArgs();
            // 获取方法名
            String methodName = point.getSignature().getName();
            // 获取方法的参数类型
            Class<?>[] parameterTypes = ((MethodSignature) point.getSignature()).getMethod().getParameterTypes();
            // 通过反射获取方法对象
            Method method = target.getClass().getMethod(methodName, parameterTypes);
            // 获取方法上的 @GlobalInterceptor 注解
            GlobalInterceptor interceptor = method.getAnnotation(GlobalInterceptor.class);
            // 如果方法上没有 @GlobalInterceptor 注解，直接返回
            if (interceptor == null) {
                return;
            }
            // 校验登录
            if (interceptor.checkLogin() || interceptor.checkAdmin()) {
                checkLogin(interceptor.checkAdmin());
            }
            // 如果注解要求校验参数，则进行参数校验
            if (interceptor.checkParams()) {
                validateParams(method, args);
            }
        } catch (BusinessException e) {
            // 记录业务异常日志并重新抛出
            logger.error("全局拦截器异常", e);
            throw e;
        } catch (Throwable e) {
            // 记录未知异常日志并抛出业务异常
            logger.error("全局拦截器异常", e);
            throw new BusinessException(ResponseCodeEnum.CODE_500);
        }
    }

    /**
     * 检查用户登录状态及权限
     * 此方法用于验证当前用户是否已登录，以及在需要时验证用户是否具有管理员权限
     *
     * @param checkAdmin 一个布尔值，指示是否需要检查用户是否为管理员
     * @throws BusinessException 如果用户未登录或需要管理员权限但当前用户不是管理员，则抛出此异常
     */
    private void checkLogin(Boolean checkAdmin) {
        // 获取当前请求的HttpServletRequest对象
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        // 获取会话对象
        HttpSession session = request.getSession();
        // 从会话中获取用户信息
        SessionWebUserDto userDto = (SessionWebUserDto) session.getAttribute(Constants.SESSION_KEY);

        // 如果会话中没有用户信息，则抛出异常，表示用户未登录
        if (userDto == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }

        // 如果需要检查管理员权限，且当前用户不是管理员，则抛出异常
        if (checkAdmin && !userDto.getIsAdmin()) {
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }
    }

    /**
     * 校验参数
     *
     * @param m 被拦截的方法
     * @param arguments 被拦截的方法的参数值数组
     */
    private void validateParams(Method m, Object[] arguments) {
        // 获取方法的所有参数
        Parameter[] parameters = m.getParameters();
        // 遍历每一个参数
        for (int i = 0; i < parameters.length; ++i) {
            Parameter parameter = parameters[i]; // 当前参数
            Object value = arguments[i]; // 当前参数的值
            // 获取参数上的 @VerifyParam 注解
            VerifyParam verifyParam = parameter.getAnnotation(VerifyParam.class);
            // 如果参数上没有 @VerifyParam 注解，则跳过该参数
            if (verifyParam == null) {
                continue;
            }
            // 如果参数是基本数据类型
            if (ArrayUtils.contains(TYPE_BASE, parameter.getParameterizedType().getTypeName())) {
                // 对基本数据类型的参数值进行校验
                checkValue(value, verifyParam);
            } else {
                // 如果参数是对象类型，对对象类型的参数值进行校验
                checkObjValue(parameter, value);
            }
        }
    }

    /**
     * 通过反射机制获取对象的字段，并检查字段上的 @VerifyParam 注解，验证字段的值是否符合注解定义的条件。
     *
     * @param parameter Parameter 参数
     * @param value 参数的值
     */
    private void checkObjValue(Parameter parameter, Object value) {
        try {
            // 获取参数的类型名称
            String typeName = parameter.getParameterizedType().getTypeName();
            // 根据类型名称获取类对象
            Class<?> classz = Class.forName(typeName);
            // 获取类的所有字段
            Field[] fields = classz.getDeclaredFields();
            // 遍历每一个字段
            for (Field field : fields) {
                // 获取字段上的 @VerifyParam 注解
                VerifyParam fieldVerifyParam = field.getAnnotation(VerifyParam.class);
                // 如果字段上没有 @VerifyParam 注解，则跳过该字段
                if (fieldVerifyParam == null) {
                    continue;
                }
                // 设置字段可访问
                field.setAccessible(true);
                // 获取value对象中该字段的值
                Object resultValue = field.get(value);
                // 验证字段的值
                checkValue(resultValue, fieldVerifyParam);
            }
        } catch (BusinessException e) {
            logger.error("校验参数失败", e);
            throw e;
        } catch (Exception e) {
            logger.error("校验参数失败", e);
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
    }

    /**
     * 校验值是否符合给定的验证参数
     *
     * @param value 要校验的值，可以是任何类型，但通常为字符串
     * @param verifyParam 包含校验规则的对象，定义了值应遵循的验证参数
     * @throws BusinessException 当值不符合验证参数时抛出的异常
     */
    private void checkValue(Object value, VerifyParam verifyParam) {
        // 判断值是否为空
        boolean isEmpty = value == null || StringTools.isEmpty(value.toString());
        // 计算值的长度
        int length = value == null ? 0 : value.toString().length();

        // 校验空
        if (isEmpty && verifyParam.required()) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        // 校验长度
        if (!isEmpty &&
            (verifyParam.max() != -1 && verifyParam.max() < length ||
            verifyParam.min() != -1 && verifyParam.min() > length)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        // 校验正则
        if (!isEmpty &&
            !StringTools.isEmpty(verifyParam.regex().getRegex()) &&
            !VerifyUtils.verify(verifyParam.regex(), String.valueOf(value))) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
    }
}

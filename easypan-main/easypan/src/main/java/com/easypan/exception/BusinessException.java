package com.easypan.exception;

import com.easypan.entity.enums.ResponseCodeEnum;
import lombok.Getter;


public class BusinessException extends RuntimeException {

    @Getter
    private ResponseCodeEnum codeEnum;

    @Getter
    private Integer code;

    private String message;

    /**
     * 使用异常信息和异常原因构造 BusinessException 实例。
     *
     * @param message 异常信息
     * @param e       异常原因
     */
    public BusinessException(String message, Throwable e) {
        // 调用父类RuntimeException的构造函数，传入异常信息和异常原因
        super(message, e);
        // 将传入的异常信息赋值给当前对象的message属性
        this.message = message;
    }


    /**
     * 构造一个 BusinessException 实例。
     *
     * @param message 异常信息
     */
    public BusinessException(String message) {
        // 调用父类RuntimeException的构造函数，传入异常信息
        super(message);

        // 将传入的异常信息赋值给当前对象的message属性
        this.message = message;
    }


    public BusinessException(Throwable e) {
        super(e);
    }

    /**
     * 使用ResponseCodeEnum枚举对象构造BusinessException实例。
     *
     * @param codeEnum ResponseCodeEnum枚举对象，用于指定异常的错误码和消息内容
     */
    public BusinessException(ResponseCodeEnum codeEnum) {
        // 调用父类构造函数，传入codeEnum对应的消息内容
        super(codeEnum.getMsg());

        // 将传入的ResponseCodeEnum对象赋值给当前对象的codeEnum属性
        this.codeEnum = codeEnum;

        // 将传入的ResponseCodeEnum对象的代码值赋值给当前对象的code属性
        this.code = codeEnum.getCode();

        // 将传入的ResponseCodeEnum对象的消息内容赋值给当前对象的message属性
        this.message = codeEnum.getMsg();
    }


    /**
     * 使用错误码和异常信息构造BusinessException实例。
     *
     * @param code    错误码
     * @param message 异常信息
     */
    public BusinessException(Integer code, String message) {
        // 调用父类RuntimeException的构造函数，传入异常信息
        super(message);

        // 将传入的错误码赋值给当前对象的code属性
        this.code = code;

        // 将传入的异常信息赋值给当前对象的message属性
        this.message = message;
    }


    @Override
    public String getMessage() {
        return message;
    }

    /**
     * 重写fillInStackTrace方法以优化业务异常处理的性能.
     * 此方法返回当前异常实例而不填充堆栈跟踪，因为业务异常通常不需要详细的堆栈信息.
     * 这样做可以提高系统效率，减少不必要的性能开销.
     *
     * @return 当前异常实例，不包含堆栈跟踪信息.
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}

package com.inkos.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    BAD_REQUEST(40000, "请求参数错误"),
    UNAUTHORIZED(40100, "未登录或 Token 已过期"),
    FORBIDDEN(40300, "权限不足"),
    NOT_FOUND(40400, "资源不存在"),
    EMAIL_ALREADY_REGISTERED(40901, "邮箱已注册"),
    EMAIL_NOT_ALLOWED(40902, "邮箱不在白名单中"),
    VERIFICATION_CODE_INVALID(40903, "验证码错误或已过期"),
    VERIFICATION_CODE_TOO_FREQUENT(40904, "验证码发送太频繁"),
    USER_DISABLED(40905, "账户已被禁用"),
    INVALID_CREDENTIALS(40101, "邮箱或密码错误"),
    INVALID_EMAIL(40001, "邮箱格式不正确"),
    INVALID_CODE(40002, "验证码错误"),
    INVALID_PASSWORD(40003, "密码格式不正确"),
    CONFLICT(40900, "资源冲突"),
    SERVICE_CONFIG_INVALID(42201, "服务配置无效"),
    RATE_LIMITED(42900, "请求太频繁"),
    INTERNAL_ERROR(50000, "服务器内部错误");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}

package com.inkos.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getHttpStatus() {
        return switch (errorCode) {
            case BAD_REQUEST, EMAIL_ALREADY_REGISTERED, EMAIL_NOT_ALLOWED,
                 VERIFICATION_CODE_INVALID, VERIFICATION_CODE_TOO_FREQUENT,
                 SERVICE_CONFIG_INVALID -> 400;
            case UNAUTHORIZED, INVALID_CREDENTIALS -> 401;
            case FORBIDDEN, USER_DISABLED -> 403;
            case NOT_FOUND -> 404;
            case RATE_LIMITED -> 429;
            case INTERNAL_ERROR -> 500;
        };
    }
}

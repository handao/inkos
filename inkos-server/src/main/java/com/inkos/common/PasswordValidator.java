package com.inkos.common;

import com.inkos.exception.BusinessException;
import com.inkos.exception.ErrorCode;

public class PasswordValidator {
  public static void validate(String password) {
    if (password == null || password.length() < 8) {
      throw new BusinessException(ErrorCode.INVALID_PASSWORD, "密码至少8位");
    }
    if (!password.matches(".*[A-Z].*")) {
      throw new BusinessException(ErrorCode.INVALID_PASSWORD, "密码需包含大写字母");
    }
    if (!password.matches(".*[a-z].*")) {
      throw new BusinessException(ErrorCode.INVALID_PASSWORD, "密码需包含小写字母");
    }
    if (!password.matches(".*\\d.*")) {
      throw new BusinessException(ErrorCode.INVALID_PASSWORD, "密码需包含数字");
    }
  }
}

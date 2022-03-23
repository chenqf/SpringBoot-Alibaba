package com.maple.foundation.base.exception;

/**
 * @author qifeng.b.chen
 * @version 1.0
 * @date 2022/1/24-14:58
 * @since 1.8
 */
public class AuthException extends RuntimeException {
  public AuthException() {}

  public AuthException(String message) {
    super(message);
  }
}

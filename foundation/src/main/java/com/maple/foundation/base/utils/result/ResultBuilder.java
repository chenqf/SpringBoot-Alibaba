package com.maple.foundation.base.utils.result;

import org.springframework.validation.BindingResult;

/**
 * @author qifeng.b.chen
 * @version 1.0
 * @date 2022/1/24-15:13
 * @since 1.8
 */
public class ResultBuilder {

  public static Result exception(BindingResult bindingResult) {
    return new Result(403, "参数有误，请检查后重试", bindingResult.getModel());
  }

  public static Result exception(Exception e) {
    return new Result(500, e.getMessage());
  }

  public static Result exception(int code, String message) {
    return new Result(code, message);
  }

  public static Result exception(int code, String message, Object data) {
    return new Result(code, message, data);
  }

  public static Result success(Object o) {
    return new Result(200, "", o);
  }
}

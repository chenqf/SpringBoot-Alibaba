package com.maple.foundation.base.aop;

import com.maple.foundation.base.exception.AuthException;
import com.maple.foundation.base.utils.result.Result;
import com.maple.foundation.base.utils.result.ResultBuilder;
import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author qifeng.b.chen
 * @version 1.0
 * @date 2022/1/24-11:49
 * @since 1.8
 */
@RestControllerAdvice
public class ControllerGlobalHandler {
  /** 权限异常 */
  @ExceptionHandler(AuthException.class)
  public Result authException(HttpServletRequest request, AuthException e) {
    e.printStackTrace();
    String message = StringUtils.isBlank(e.getMessage()) ? "登录信息有误，请登录后重试" : e.getMessage();
    return ResultBuilder.exception(401, message);
  }

  /** 针对Validate校验异常 */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public Result methodArgumentNotValidException(
      HttpServletRequest request, MethodArgumentNotValidException e) {
    e.printStackTrace();
    return ResultBuilder.exception(403, "参数有误", e.getBindingResult());
  }

  /** RequestParam 参数缺失 */
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public Result missParameterException(
      HttpServletRequest request, MissingServletRequestParameterException e) {
    e.printStackTrace();
    String data = "入参 " + e.getParameterType() + " " + e.getParameterName() + " 不可为空";
    return ResultBuilder.exception(403, "参数有误", data);
  }

  /** url_params | path_params 参数类型转换失败 */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public Result argumentTypeMismatchException(
      HttpServletRequest request, MethodArgumentTypeMismatchException e) {
    e.printStackTrace();
    String data =
        "将入参 "
            + e.getName()
            + " = "
            + e.getValue()
            + " 转换为 "
            + e.getRequiredType().getSimpleName()
            + " 失败";
    return ResultBuilder.exception(403, "参数有误", data);
  }

  /** 请求参数与指定参数不匹配导致请求消息不可读 @RequestBody 无对应值 */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public Result httpMessageNotReadableException(
      HttpServletRequest request, HttpMessageNotReadableException e) throws IOException {
    e.printStackTrace();
    return ResultBuilder.exception(403, "参数有误", e.getMessage());
  }

  /** SQL异常 */
  @ExceptionHandler(DuplicateKeyException.class)
  public Result sqlException(DuplicateKeyException e) {
    e.printStackTrace();
    return ResultBuilder.exception(501, "数据异常，请检查后重试", e.getMessage());
  }

  /** SQL异常 */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public Result sqlException(DataIntegrityViolationException e) {
    e.printStackTrace();
    return ResultBuilder.exception(501, "数据异常，请检查后重试", e.getMessage());
  }

  /** 其他异常 */
  @ExceptionHandler(Exception.class)
  public Result exception(Exception e) {
    e.printStackTrace();
    return ResultBuilder.exception(500, e.getMessage());
  }

  // TODO 数据库异常
}

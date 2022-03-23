package com.maple.foundation.base.aop;

import com.maple.foundation.base.utils.result.Result;
import com.maple.foundation.base.utils.result.ResultBuilder;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author qifeng.b.chen
 * @version 1.0
 * @date 2022/1/24-16:27
 * @since 1.8
 */
@RestControllerAdvice
public class ResponseGlobalHandler implements ResponseBodyAdvice {
  @Override
  public boolean supports(MethodParameter returnType, Class converterType) {
    return true;
  }

  @Override
  public Object beforeBodyWrite(
      Object body,
      MethodParameter returnType,
      MediaType selectedContentType,
      Class selectedConverterType,
      ServerHttpRequest request,
      ServerHttpResponse response) {
    if (body instanceof Result) {
      return body;
    }
    return ResultBuilder.success(body);
  }
}

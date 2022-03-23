package com.maple.common.mybatis.enumeration;

import com.maple.common.mybatis.funtion.Desensitizer;

/**
 * @author qifeng.b.chen
 * @version 1.0
 * @date 2022/3/18-18:14
 * @since 1.8
 */
public enum DesensitizeEnum {
  // 用户名
  USER_NAME(s -> s.replaceAll("(\\S)\\S(\\S*)", "$1*$2")),
  // 身份证
  ID_CARD(s -> s.replaceAll("(\\d{4})\\d{10}(\\w{4})", "$1****$2")),
  // 电话号码
  PHONE(s -> s.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2")),
  // 地址
  ADDRESS(s -> s.replaceAll("(\\S{8})\\S{4}(\\S*)\\S{4}", "$1****$2****"));

  /** 执行器 */
  private final Desensitizer desensitizer;

  DesensitizeEnum(Desensitizer desensitizer) {
    this.desensitizer = desensitizer;
  }

  public Desensitizer getDesensitizer() {
    return this.desensitizer;
  }
}

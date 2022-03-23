package com.maple.common.mybatis.annotation;

import com.maple.common.mybatis.enumeration.DesensitizeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author qifeng.b.chen
 * @version 1.0
 * @date 2022/3/18-16:59
 * @since 1.8
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Desensitize {
  DesensitizeEnum value();
}

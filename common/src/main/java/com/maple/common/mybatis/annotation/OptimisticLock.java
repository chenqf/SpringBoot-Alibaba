package com.maple.common.mybatis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author qifeng.b.chen
 * @version 1.0
 * @date 2022/3/19-21:24
 * @since 1.8
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface OptimisticLock {
  // Bean 中的字段
  String value() default "version";
  // DB 中的字段 不传相当于 column = value
  String column() default "";
}

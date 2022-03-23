package com.maple.common.datasource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识使用何种数据源
 *
 * @author qifeng.b.chen
 * @version 1.0
 * @date 2022/3/22-11:51
 * @since 1.8
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RoutingWithMaster {}

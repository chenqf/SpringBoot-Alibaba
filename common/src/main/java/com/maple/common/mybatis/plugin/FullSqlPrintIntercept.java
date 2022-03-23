package com.maple.common.mybatis.plugin;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.core.Ordered;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author qifeng.b.chen
 * @version 1.0
 * @date 2022/3/18-13:21
 * @since 1.8
 */
@Slf4j
@Intercepts({
  @Signature(
      type = StatementHandler.class,
      method = "query",
      args = {Statement.class, ResultHandler.class}),
  @Signature(
      type = StatementHandler.class,
      method = "update",
      args = {Statement.class}),
  @Signature(
      type = StatementHandler.class,
      method = "batch",
      args = {Statement.class})
})
public class FullSqlPrintIntercept implements Interceptor, Ordered {
  private Configuration configuration = null;

  private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT_THREAD_LOCAL =
      new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
          return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        }
      };

  @Override
  public Object intercept(Invocation invocation) throws Throwable {

    Object target = invocation.getTarget();
    long startTime = System.currentTimeMillis();
    String sql = "";
    try {
      sql = this.getSql(target);
      return invocation.proceed();
    } finally {
      long endTime = System.currentTimeMillis();
      long sqlCost = endTime - startTime;
      log.info("SQL:{}    执行耗时={}", sql, sqlCost + "ms");
    }
  }

  private Configuration getConfiguration(StatementHandler statementHandler)
      throws IllegalAccessException {
    if (this.configuration == null) {
      final DefaultParameterHandler parameterHandler =
          (DefaultParameterHandler) statementHandler.getParameterHandler();
      Field configurationField =
          ReflectionUtils.findField(parameterHandler.getClass(), "configuration");
      if (configurationField != null) {
        ReflectionUtils.makeAccessible(configurationField);
        this.configuration = (Configuration) configurationField.get(parameterHandler);
      }
    }
    return this.configuration;
  }

  private String getSql(Object target) {
    try {
      StatementHandler statementHandler = (StatementHandler) target;
      BoundSql boundSql = statementHandler.getBoundSql();
      Configuration configuration = this.getConfiguration(statementHandler);
      // 替换参数格式化Sql语句，去除换行符
      return getFullSql(boundSql, configuration);
    } catch (Exception e) {
      log.warn("get sql error {}", target, e);
    }
    return "";
  }

  private String getFullSql(BoundSql boundSql, Configuration configuration) {
    String sql = boundSql.getSql();
    if (sql == null || "".equals(sql)) {
      return "";
    }
    sql = beautifySql(sql);
    // 获取入参对象
    Object parameterObject = boundSql.getParameterObject();
    // 获取 #{}
    List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
    // 存在入参
    if (!parameterMappings.isEmpty() && parameterObject != null) {
      TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
      // 存在参数对应的类型处理器 - 只有一个参数
      if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
        sql = simpleSqlTypeReplace(sql, parameterObject);
      }
      // 对象
      else {
        // 获取传入的对象本身
        MetaObject metaObject = configuration.newMetaObject(parameterObject);
        for (ParameterMapping parameterMapping : parameterMappings) {
          // 获取#{}中的属性名
          String propertyName = parameterMapping.getProperty();
          // 判断入参中是否有对应的属性
          if (metaObject.hasGetter(propertyName)) {
            Object value = metaObject.getValue(propertyName);
            if (value != null) {
              sql = simpleSqlTypeReplace(sql, value);
            }
          }
          // 动态参数
          else if (boundSql.hasAdditionalParameter(propertyName)) {
            Object value = boundSql.getAdditionalParameter(propertyName);
            if (value != null) {
              sql = simpleSqlTypeReplace(sql, value);
            }
          }
        }
      }
    }
    return sql;
  }

  private String simpleSqlTypeReplace(String sql, Object parameterObject) {
    String result;
    if (parameterObject instanceof String) {
      result = "'" + parameterObject.toString() + "'";
    } else if (parameterObject instanceof Date) {
      result = "'" + DATE_FORMAT_THREAD_LOCAL.get().format(parameterObject) + "'";
    } else {
      result = parameterObject.toString();
    }
    return sql.replaceFirst("\\?", result);
  }

  private String beautifySql(String sql) {
    return sql.replaceAll("[\\s\n]+", " ");
  }

  /**
   * 设置优先级最低
   *
   * @return 最小数
   */
  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }
}

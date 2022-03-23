package com.maple.common.mybatis.plugin;

import com.maple.common.mybatis.annotation.Desensitize;
import com.maple.common.mybatis.enumeration.DesensitizeEnum;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Stream;

/**
 * 查询时进行数据脱敏-感觉用处不大
 *
 * @author qifeng.b.chen
 * @version 1.0
 * @date 2022/3/18-16:44
 * @since 1.8
 */
@Intercepts(
    @Signature(
        type = ResultSetHandler.class,
        method = "handleResultSets",
        args = {Statement.class}))
public class DesensitizeIntercept implements Interceptor {
  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    // 获取结果集
    List<Object> records = (List<Object>) invocation.proceed();
    records.forEach(this::dataMasking);
    return records;
  }

  private void dataMasking(Object source) {
    Class<?> sourceClass = source.getClass();
    MetaObject metaObject = SystemMetaObject.forObject(source);
    Stream.of(sourceClass.getDeclaredFields())
        .filter(filed -> filed.isAnnotationPresent(Desensitize.class))
        .forEach(filed -> doDataMasking(metaObject, filed));
  }

  private void doDataMasking(MetaObject metaObject, Field filed) {
    String name = filed.getName();
    Object value = metaObject.getValue(name);
    if (value != null && metaObject.getGetterType(name) == String.class) {
      Desensitize annotation = filed.getAnnotation(Desensitize.class);
      DesensitizeEnum type = annotation.value();
      Object o = type.getDesensitizer().apply((String) value);
      metaObject.setValue(name, o);
    }
  }
}

package com.maple.common.mybatis.plugin;

import com.maple.common.mybatis.annotation.OptimisticLock;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.update.UpdateSet;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author qifeng.b.chen
 * @version 1.0
 * @date 2022/3/19-20:59
 * @since 1.8
 */
@Intercepts(
    @Signature(
        type = StatementHandler.class,
        method = "prepare",
        args = {Connection.class, Integer.class}))
@Slf4j
public class OptimisticLockInterceptor implements Interceptor {

  private static final String REPLACE_SEAT = "?";

  @Override
  public Object intercept(Invocation invocation)
      throws InvocationTargetException, IllegalAccessException, JSQLParserException {
    // 获取 StatementHandler，实际是 RoutingStatementHandler
    StatementHandler handler = (StatementHandler) processTarget(invocation.getTarget());
    // 包装原始对象，便于获取和设置属性
    MetaObject metaObject = SystemMetaObject.forObject(handler);
    // MappedStatement 是对SQL更高层次的一个封装，这个对象包含了执行SQL所需的各种配置信息
    MappedStatement mappedStatement =
        (MappedStatement) metaObject.getValue("delegate.mappedStatement");
    // SQL类型
    SqlCommandType sqlType = mappedStatement.getSqlCommandType();
    if (sqlType != SqlCommandType.UPDATE) {
      return invocation.proceed();
    }
    BoundSql boundSql = handler.getBoundSql();
    Object parameterObject = boundSql.getParameterObject();
    // 不存在Bean
    if (parameterObject == null) {
      return invocation.proceed();
    }
    // Bean上未指明乐观锁
    if (!parameterObject.getClass().isAnnotationPresent(OptimisticLock.class)) {
      return invocation.proceed();
    }
    // 获取乐观锁字段(Bean + DB)
    String versionKey = parameterObject.getClass().getAnnotation(OptimisticLock.class).value();
    String versionColumn = parameterObject.getClass().getAnnotation(OptimisticLock.class).column();
    if ("".equals(versionColumn)) {
      versionColumn = versionKey;
    }

    // 获取当前客户端版本号
    Field versionField = null;
    try {
      versionField = parameterObject.getClass().getDeclaredField(versionKey);
    } catch (NoSuchFieldException e) {
      log.warn("当前Bean [ " + parameterObject.getClass().getName() + " ] 未找到乐观锁字段：" + versionKey);
      return invocation.proceed();
    }
    versionField.setAccessible(true);
    Object versionValue = versionField.get(parameterObject);
    if (versionValue == null || Long.parseLong(versionValue.toString()) <= 0) {
      return invocation.proceed();
    }
    // 所有替换参数
    List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
    // 原始SQL
    String originalSql = boundSql.getSql();
    // 获取新SQL
    String newSql = getNewSql(originalSql, versionColumn, versionValue, parameterMappings);
    // 修改 BoundSql
    metaObject.setValue("delegate.boundSql.sql", newSql);

    return invocation.proceed();
  }

  private String getNewSql(
      String originalSql,
      String versionColumn,
      Object versionValue,
      List<ParameterMapping> parameterMappings)
      throws JSQLParserException {
    Statement stmt = CCJSqlParserUtil.parse(originalSql);
    if (!(stmt instanceof Update)) {
      return originalSql;
    }
    Update update = (Update) stmt;
    // set version = version + 1
    makeUpdateSet(update, versionColumn, parameterMappings);
    // where xx=xxx and version = version
    makeWhere(update, versionColumn, versionValue);

    return update.toString();
  }

  private void makeWhere(Update update, String columnName, Object columnValue) {
    Expression where = update.getWhere();
    Column column = new Column(columnName);
    EqualsTo equal = new EqualsTo();
    equal.setLeftExpression(column);
    equal.setRightExpression(new LongValue(columnValue.toString()));
    if (where == null) {
      update.setWhere(equal);
    } else {
      AndExpression and = new AndExpression(where, equal);
      update.setWhere(and);
    }
  }

  private void makeUpdateSet(
      Update update, String columnName, List<ParameterMapping> parameterMappings) {
    UpdateSet updateSet = getUpdateSetByColumnName(update, columnName);
    if (updateSet == null) {
      Column column = new Column(columnName);
      Addition add = new Addition();
      add.setLeftExpression(column);
      add.setRightExpression(new LongValue(1));
      updateSet = new UpdateSet(column, add);
      update.addUpdateSet(updateSet);
    } else {
      // 开发者已指定 version 的更新，则不作处理
      //      List<Column> allColumn = getAllColumn(update);
      //      List<Expression> allExpression = getAllExpression(update);
      //
      //      List<String> list = new ArrayList<String>();
      //      for (Expression expression : allExpression) {
      //        list.add(expression.toString());
      //      }
      //      Column column = null;
      //      ArrayList<Column> columns = updateSet.getColumns();
      //      for (Column col : columns) {
      //        if (columnName.equals(col.getColumnName())) {
      //          column = col;
      //          break;
      //        }
      //      }
      //      int index = columns.indexOf(column);
      //      Addition add = new Addition();
      //      add.setLeftExpression(column);
      //      add.setRightExpression(new LongValue(1));
      //      ArrayList<Expression> expressions = updateSet.getExpressions();
      //      expressions.set(index, add);
      //
      //      // 删除不需要的参数映射 是否需要这么做？
      //      int allIndex = allColumn.indexOf(column);
      //      int deleteIndex = -1;
      //      if (list.get(allIndex).contains(REPLACE_SEAT)) {
      //        for (int i = 0; i <= allIndex; i++) {
      //          String v = list.get(i);
      //          if (v != null && v.contains(REPLACE_SEAT)) {
      //            deleteIndex++;
      //          }
      //        }
      //      }
      //      parameterMappings.remove(deleteIndex);
    }
  }

  private UpdateSet getUpdateSetByColumnName(Update update, String columnName) {
    for (UpdateSet updateSet : update.getUpdateSets()) {
      for (Column column : updateSet.getColumns()) {
        if (column.getColumnName().equalsIgnoreCase(columnName)) {
          return updateSet;
        }
      }
    }
    return null;
  }

  private List<Column> getAllColumn(Update update) {
    List<Column> list = new ArrayList<Column>();
    for (UpdateSet updateSet : update.getUpdateSets()) {
      for (Column column : updateSet.getColumns()) {
        list.addAll(updateSet.getColumns());
      }
    }
    return list;
  }

  private List<Expression> getAllExpression(Update update) {
    List<Expression> list = new ArrayList<Expression>();
    for (UpdateSet updateSet : update.getUpdateSets()) {
      list.addAll(updateSet.getExpressions());
    }
    return list;
  }

  /** 获取代理的原始对象 */
  private static Object processTarget(Object target) {
    if (Proxy.isProxyClass(target.getClass())) {
      MetaObject mo = SystemMetaObject.forObject(target);
      return processTarget(mo.getValue("h.target"));
    }
    return target;
  }
}

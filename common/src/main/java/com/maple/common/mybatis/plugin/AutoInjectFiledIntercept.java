package com.maple.common.mybatis.plugin;

import com.maple.common.mybatis.annotation.*;
import com.maple.common.utils.StringUtil;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 追加基本信息 修改人 时间 默认值
 *
 * @author qifeng.b.chen
 * @version 1.0
 * @date 2022/3/18-11:27
 * @since 1.8
 */
@Intercepts(
    @Signature(
        type = StatementHandler.class,
        method = "prepare",
        args = {Connection.class, Integer.class}))
public class AutoInjectFiledIntercept implements Interceptor {
  /**
   * @param invocation 调用过程
   * @return 下一调用过程
   */
  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    // 获取 StatementHandler，实际是 RoutingStatementHandler
    StatementHandler handler = (StatementHandler) processTarget(invocation.getTarget());
    // 包装原始对象，便于获取和设置属性
    MetaObject metaObject = SystemMetaObject.forObject(handler);
    // MappedStatement 是对SQL更高层次的一个封装，这个对象包含了执行SQL所需的各种配置信息
    MappedStatement mappedStatement =
        (MappedStatement) metaObject.getValue("delegate.mappedStatement");
    // SQL类型
    SqlCommandType sqlType = mappedStatement.getSqlCommandType();

    if (sqlType.equals(SqlCommandType.UPDATE) || sqlType.equals(SqlCommandType.INSERT)) {
      BoundSql boundSql = handler.getBoundSql();
      // Bean 对象
      Object parameterObject = boundSql.getParameterObject();
      // 所有替换参数
      List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
      // 不存在Bean
      if (parameterObject == null) {
        return invocation.proceed();
      }
      Field[] fields = parameterObject.getClass().getDeclaredFields();
      List<Field> createTimeList = new ArrayList<>();
      List<Field> updateTimeList = new ArrayList<>();
      List<Field> createByList = new ArrayList<>();
      List<Field> updateByList = new ArrayList<>();
      List<Field> defaultValueList = new ArrayList<>();
      for (Field field : fields) {
        if (field.isAnnotationPresent(CreatedDate.class)) {
          createTimeList.add(field);
        } else if (field.isAnnotationPresent(LastModifiedDate.class)) {
          updateTimeList.add(field);
          createTimeList.add(field);
        } else if (field.isAnnotationPresent(CreatedBy.class)) {
          createByList.add(field);
        } else if (field.isAnnotationPresent(LastModifiedBy.class)) {
          updateByList.add(field);
          createByList.add(field);
        } else if (field.isAnnotationPresent(DefaultNumberValue.class)) {
          defaultValueList.add(field);
        } else if (field.isAnnotationPresent(DefaultBooleanValue.class)) {
          defaultValueList.add(field);
        } else if (field.isAnnotationPresent(DefaultStringValue.class)) {
          defaultValueList.add(field);
        }
      }
      // 原始SQL
      String originalSql = boundSql.getSql();
      String newSql =
          getNewSql(
              sqlType,
              originalSql,
              createTimeList,
              updateTimeList,
              createByList,
              updateByList,
              defaultValueList,
              parameterObject,
              parameterMappings);
      // 修改 BoundSql
      metaObject.setValue("delegate.boundSql.sql", newSql);
      return invocation.proceed();
    }
    return invocation.proceed();
  }

  private String getNewSql(
      SqlCommandType sqlType,
      String originalSql,
      List<Field> createTimeList,
      List<Field> updateTimeList,
      List<Field> createByList,
      List<Field> updateByList,
      List<Field> defaultValueList,
      Object parameterObject,
      List<ParameterMapping> parameterMappings)
      throws JSQLParserException, IllegalAccessException, NoSuchFieldException {
    if (sqlType.equals(SqlCommandType.UPDATE)) {
      return getUpdateNewSql(originalSql, updateTimeList, updateByList);
    } else if (sqlType.equals(SqlCommandType.INSERT)) {
      return getInsertNewSql(
          originalSql,
          createTimeList,
          createByList,
          defaultValueList,
          parameterObject,
          parameterMappings);
    } else {
      return originalSql;
    }
  }

  private String getInsertNewSql(
      String originalSql,
      List<Field> createTimeList,
      List<Field> createByList,
      List<Field> defaultValueList,
      Object parameterObject,
      List<ParameterMapping> parameterMappings)
      throws JSQLParserException, IllegalAccessException, NoSuchFieldException {
    HttpServletRequest request =
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    Statement stmt = CCJSqlParserUtil.parse(originalSql);
    if (!(stmt instanceof Insert)) {
      return originalSql;
    }
    Insert insert = (Insert) stmt;
    List<Column> columns = insert.getColumns();
    List<String> columnNames =
        columns.stream().map(i -> i.getColumnName()).collect(Collectors.toList());
    List<Expression> expressions = ((ExpressionList) insert.getItemsList()).getExpressions();

    // 默认值
    setDefaultValue(defaultValueList, columnNames, columns, expressions, parameterObject);
    // 创建人
    setCreateBy(createByList, columnNames, columns, expressions, request, parameterObject);
    // 创建时间
    setCreateTime(createTimeList, columnNames, columns, expressions, parameterObject);

    return insert.toString();
  }

  private void setDefaultValue(
      List<Field> defaultValueList,
      List<String> columnNames,
      List<Column> columns,
      List<Expression> expressions,
      Object parameterObject)
      throws IllegalAccessException {
    for (Field field : defaultValueList) {
      String columnName = "";
      Object value = null;
      boolean isBoolean = field.isAnnotationPresent(DefaultBooleanValue.class);
      boolean isNumber = field.isAnnotationPresent(DefaultNumberValue.class);
      boolean isString = field.isAnnotationPresent(DefaultStringValue.class);
      if (isBoolean) {
        columnName = field.getAnnotation(DefaultBooleanValue.class).column();
        value = field.getAnnotation(DefaultBooleanValue.class).value();
      } else if (isNumber) {
        columnName = field.getAnnotation(DefaultNumberValue.class).column();
        value = field.getAnnotation(DefaultNumberValue.class).value();
      } else if (isString) {
        columnName = field.getAnnotation(DefaultStringValue.class).column();
        value = field.getAnnotation(DefaultStringValue.class).value();
      }
      columnName = "".equals(columnName) ? StringUtil.humpToLine(field.getName()) : columnName;
      // 原sql中没有该字段
      if (!columnNames.contains(columnName)) {
        Expression exp = null;
        if (isString) {
          exp = new StringValue(value.toString());
        } else if (isNumber) {
          exp = new LongValue(value.toString());
        } else if (isBoolean) {
          exp = new LongValue(value.toString());
        }
        columns.add(new Column(columnName));
        expressions.add(exp);
      } else {
        field.setAccessible(true);
        Object o = field.get(parameterObject);
        if (o == null) {
          field.set(parameterObject, value);
        }
      }
    }
  }

  private void setCreateBy(
      List<Field> createByList,
      List<String> columnNames,
      List<Column> columns,
      List<Expression> expressions,
      HttpServletRequest request,
      Object parameterObject)
      throws IllegalAccessException {
    Object currentUser = request.getAttribute("userId");
    if (currentUser == null) {
      return;
    }
    for (Field field : createByList) {
      String columnName = "";
      if (field.isAnnotationPresent(CreatedBy.class)) {
        columnName = field.getAnnotation(CreatedBy.class).column();
      } else if (field.isAnnotationPresent(LastModifiedBy.class)) {
        columnName = field.getAnnotation(LastModifiedBy.class).column();
      }
      columnName = "".equals(columnName) ? StringUtil.humpToLine(field.getName()) : columnName;
      // 原sql中没有该字段
      if (!columnNames.contains(columnName)) {
        columns.add(new Column(columnName));
        expressions.add(new LongValue(currentUser.toString()));
      } else {
        field.setAccessible(true);
        Object o = field.get(parameterObject);
        if (o == null) {
          field.set(parameterObject, currentUser);
        }
      }
    }
  }

  private void setCreateTime(
      List<Field> createTimeList,
      List<String> columnNames,
      List<Column> columns,
      List<Expression> expressions,
      Object parameterObject)
      throws IllegalAccessException {
    for (Field field : createTimeList) {
      String columnName = "";
      if (field.isAnnotationPresent(CreatedDate.class)) {
        columnName = field.getAnnotation(CreatedDate.class).column();
      } else if (field.isAnnotationPresent(LastModifiedDate.class)) {
        columnName = field.getAnnotation(LastModifiedDate.class).column();
      }
      columnName = "".equals(columnName) ? StringUtil.humpToLine(field.getName()) : columnName;
      // 原sql中没有该字段
      if (!columnNames.contains(columnName)) {
        columns.add(new Column(columnName));
        Expression exp =
            new StringValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
        expressions.add(exp);
      } else {
        field.setAccessible(true);
        Object o = field.get(parameterObject);
        if (o == null) {
          field.set(parameterObject, new Date());
        }
      }
    }
  }

  private String getUpdateNewSql(
      String originalSql, List<Field> updateTimeList, List<Field> updateByList)
      throws JSQLParserException {
    HttpServletRequest request =
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    Statement stmt = CCJSqlParserUtil.parse(originalSql);
    if (!(stmt instanceof Update)) {
      return originalSql;
    }
    Update update = (Update) stmt;
    List<Column> updateAllColumn = getUpdateAllColumn(update);
    List<String> updateAllColumnNames =
        updateAllColumn.stream().map(Column::getColumnName).collect(Collectors.toList());
    // 最后修改时间
    List<Field> updateTimeFields =
        updateTimeList.stream()
            .filter(
                i -> {
                  String column = i.getAnnotation(LastModifiedDate.class).column();
                  column = "".equals(column) ? StringUtil.humpToLine(i.getName()) : column;
                  return !updateAllColumnNames.contains(column);
                })
            .collect(Collectors.toList());
    for (Field updateTimeField : updateTimeFields) {
      String columnName = StringUtil.humpToLine(updateTimeField.getName());
      Column column = new Column(columnName);
      Expression exp =
          new StringValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
      update.addUpdateSet(column, exp);
    }
    // 最后修改人
    List<Field> updateByFields =
        updateByList.stream()
            .filter(
                i -> {
                  String column = i.getAnnotation(LastModifiedBy.class).column();
                  column = "".equals(column) ? StringUtil.humpToLine(i.getName()) : column;
                  return !updateAllColumnNames.contains(column);
                })
            .collect(Collectors.toList());

    for (Field updateByField : updateByFields) {
      Object currentUser = request.getAttribute("userId");
      if (currentUser == null) {
        break;
      }
      String columnName = StringUtil.humpToLine(updateByField.getName());
      Column column = new Column(columnName);
      Expression exp = new LongValue(currentUser.toString());
      update.addUpdateSet(column, exp);
    }
    return update.toString();
  }

  /** 获取代理的原始对象 */
  private static Object processTarget(Object target) {
    if (Proxy.isProxyClass(target.getClass())) {
      MetaObject mo = SystemMetaObject.forObject(target);
      return processTarget(mo.getValue("h.target"));
    }
    return target;
  }

  private List<Column> getUpdateAllColumn(Update update) {
    List<Column> list = new ArrayList<Column>();
    for (UpdateSet updateSet : update.getUpdateSets()) {
      for (Column column : updateSet.getColumns()) {
        list.addAll(updateSet.getColumns());
      }
    }
    return list;
  }
}

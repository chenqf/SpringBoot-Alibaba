package com.maple.common.mybatis.plugin;

import com.maple.common.datasource.DynamicDataSourceEnum;
import com.maple.common.datasource.RoutingDataSourceContent;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Locale;

/**
 * @author qifeng.b.chen
 * @version 1.0
 * @date 2022/3/18-13:21
 * @since 1.8
 */
@Slf4j
@Intercepts({
  @Signature(
      type = Executor.class,
      method = "update",
      args = {MappedStatement.class, Object.class}),
  @Signature(
      type = Executor.class,
      method = "query",
      args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
  @Signature(
      type = Executor.class,
      method = "query",
      args = {
        MappedStatement.class,
        Object.class,
        RowBounds.class,
        ResultHandler.class,
        CacheKey.class,
        BoundSql.class
      })
})
public class DynamicDataSourceIntercept implements Interceptor {
  /** 用于判断sql中是否有更新语句 */
  private static final String REGEX = ".*insert\\u0020.*|.*update\\u0020.*|.*delete.*";

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    if (RoutingDataSourceContent.getCurrentLockValue()) {
      log.info("当前数据源已被锁定，当前使用的数据源是 [{}]", RoutingDataSourceContent.getCurrentContent().name());
      return invocation.proceed();
    }
    // 默认使用master
    DynamicDataSourceEnum nodeType = DynamicDataSourceEnum.MASTER;
    // 获取mybatis转换过来的CRUD参数
    Object[] objects = invocation.getArgs();
    // MappedStatement维护了一条<select|update|delete|insert>节点的封装
    MappedStatement mappedStatement = (MappedStatement) objects[0];
    // 当前sql类型
    SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
    // 判断是否存在活跃的事务
    boolean actualTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
    // 存在事务，避免出现更新并查询导致数据不一致的情况，强制使用master
    if (actualTransactionActive) {
      nodeType = DynamicDataSourceEnum.MASTER;
    } else {
      // 真实sql及参数
      BoundSql boundSql = mappedStatement.getSqlSource().getBoundSql(objects[1]);
      // 将字符装换为简体中文的小写
      String sql = boundSql.getSql().toLowerCase(Locale.CHINA).replaceAll("\\t\\n\\r", " ");
      // 是否可能存在更新的情况
      boolean isUpdate = sql.matches(REGEX);
      // 查询sql使用slave
      if (!isUpdate && sqlCommandType.equals(SqlCommandType.SELECT)) {
        nodeType = DynamicDataSourceEnum.SLAVE;
      } else {
        nodeType = DynamicDataSourceEnum.MASTER;
      }
    }
    // 设定当前sql查询的datasource
    RoutingDataSourceContent.setCurrentContent(nodeType);
    try {
      return invocation.proceed();
    } finally {
      log.info(
          "当前是否存在事务 [{}] 使用的节点为 [{}], sql类型为 [{}].. ",
          actualTransactionActive,
          RoutingDataSourceContent.getCurrentContent().name(),
          mappedStatement.getSqlCommandType().name());
      RoutingDataSourceContent.close();
    }
  }
}

package com.maple.common.datasource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 实现选择数据源切面
 *
 * @author qifeng.b.chen
 * @version 1.0
 * @date 2022/3/22-11:56
 * @since 1.8
 */
@Aspect
@Component
public class RoutingAspect {

  @Around("@annotation(withMaster)")
  public Object routingWithDataSource(ProceedingJoinPoint joinPoint, RoutingWithMaster withMaster)
      throws Throwable {
    DynamicDataSourceEnum nodeType = DynamicDataSourceEnum.MASTER;
    if (!RoutingDataSourceContent.getCurrentLockValue()) {
      RoutingDataSourceContent.setCurrentContent(nodeType);
      RoutingDataSourceContent.lock();
    } else if (RoutingDataSourceContent.getCurrentContent() != nodeType) {
      throw new Exception("当前数据源已被Lock，无法变更为 [ " + nodeType.name() + " ]");
    }
    try {
      return joinPoint.proceed();
    } catch (Exception exception) {
      throw exception;
    } finally {
      RoutingDataSourceContent.close();
      RoutingDataSourceContent.unlock();
    }
  }
}

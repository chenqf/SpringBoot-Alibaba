package com.maple.common.datasource;

import lombok.extern.slf4j.Slf4j;

/**
 * @author qifeng.b.chen
 * @version 1.0
 * @date 2022/3/22-11:19
 * @since 1.8
 */
@Slf4j
public class RoutingDataSourceContent {
  /** 存放当前数据库连接源标识 */
  private static final ThreadLocal<String> DATASOURCE_LOCAL = new ThreadLocal<>();
  /** 是否可以修改数据库连接源标识--锁 */
  private static final ThreadLocal<Boolean> DATASOURCE_LOCAL_LOCK = new ThreadLocal<>();

  public static DynamicDataSourceEnum getCurrentContent() {
    String s = DATASOURCE_LOCAL.get();
    if (s == null) {
      return DynamicDataSourceEnum.MASTER;
    }
    try {
      return DynamicDataSourceEnum.valueOf(s);
    } catch (Exception exception) {
      exception.printStackTrace();
      return DynamicDataSourceEnum.MASTER;
    }
  }

  public static void setCurrentContent(DynamicDataSourceEnum nodeType) {
    if (!getCurrentLockValue()) {
      DATASOURCE_LOCAL.set(nodeType.name());
    } else {
      log.warn("当前DataSource被指定为 [{}] ,且当前已被锁定无法修改", getCurrentContent().name());
    }
  }

  public static boolean getCurrentLockValue() {
    Boolean flg = DATASOURCE_LOCAL_LOCK.get();
    return flg != null && flg;
  }

  public static void lock() {
    System.out.println("加锁");
    DATASOURCE_LOCAL_LOCK.set(true);
  }

  public static void unlock() {
    System.out.println("解锁");
    DATASOURCE_LOCAL_LOCK.set(false);
  }

  public static void close() {
    System.out.println("清空");
    DATASOURCE_LOCAL.remove();
  }
}

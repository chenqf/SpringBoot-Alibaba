package com.maple.common.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author qifeng.b.chen
 * @version 1.0
 * @date 2022/3/22-11:09
 * @since 1.8
 */
public class RoutingDataSource extends AbstractRoutingDataSource {
  @Override
  protected Object determineCurrentLookupKey() {
    return RoutingDataSourceContent.getCurrentContent();
  }
}

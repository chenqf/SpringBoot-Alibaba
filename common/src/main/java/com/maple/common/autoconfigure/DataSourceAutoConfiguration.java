package com.maple.common.autoconfigure;

import com.maple.common.datasource.DynamicDataSourceEnum;
import com.maple.common.datasource.RoutingAspect;
import com.maple.common.datasource.RoutingDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;

/**
 * @author qifeng.b.chen
 * @version 1.0
 * @date 2022/3/22-11:03
 * @since 1.8
 */
@Slf4j
@Import({RoutingAspect.class})
@Configuration
public class DataSourceAutoConfiguration {

  @Bean
  @ConfigurationProperties(prefix = "spring.datasource.druid.master")
  public DataSource masterDataSource() {
    log.info("create master datasource...");
    return DataSourceBuilder.create().build();
  }

  @Bean
  @ConfigurationProperties(prefix = "spring.datasource.druid.slave")
  public DataSource slaveDataSource() {
    log.info("create slave datasource...");
    return DataSourceBuilder.create().build();
  }

  @Bean
  @Primary
  public DataSource primaryDataSource(
      @Autowired @Qualifier("masterDataSource") DataSource masterDataSource,
      @Autowired @Qualifier("slaveDataSource") DataSource slaveDataSource) {
    RoutingDataSource routingDataSource = new RoutingDataSource();

    HashMap<Object, Object> map = new HashMap<>();
    map.put(DynamicDataSourceEnum.MASTER, masterDataSource);
    map.put(DynamicDataSourceEnum.SLAVE, slaveDataSource);

    routingDataSource.setTargetDataSources(map);

    return routingDataSource;
  }
}

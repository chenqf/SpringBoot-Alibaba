package com.maple.foundation.config;

import com.maple.common.mybatis.plugin.*;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qifeng.b.chen
 * @version 1.0
 * @date 2022/3/18-11:58
 * @since 1.8
 */
@Configuration
public class MyBatisConfig {
  @Bean
  ConfigurationCustomizer mybatisConfigurationCustomizer() {
    return new ConfigurationCustomizer() {
      @Override
      public void customize(org.apache.ibatis.session.Configuration configuration) {
        configuration.addInterceptor(new AutoInjectFiledIntercept());
        configuration.addInterceptor(new OptimisticLockInterceptor());
        configuration.addInterceptor(new DesensitizeIntercept());
        configuration.addInterceptor(new DynamicDataSourceIntercept());
        configuration.addInterceptor(new FullSqlPrintIntercept());
      }
    };
  }
}

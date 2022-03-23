package com.maple.foundation;

import com.maple.common.autoconfigure.AutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Import({AutoConfiguration.class})
@MapperScan("com.maple.foundation.mapper")
@EnableCaching
@EnableTransactionManagement
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class FoundationApplication {
  // https://www.jb51.net/article/213434.htm
  static {
    System.setProperty("druid.mysql.usePingMethod", "false");
  }

  public static void main(String[] args) {

    ConfigurableApplicationContext context =
        SpringApplication.run(FoundationApplication.class, args);
  }
}

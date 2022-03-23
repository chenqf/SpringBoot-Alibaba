package com.maple.order.base.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author qifeng.b.chen
 * @version 1.0
 * @date 2022/3/15-15:52
 * @since 1.8
 */
@Configuration
public class RestTemplateConfig {
  @Bean
  @LoadBalanced
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    // TODO 设置超时时间
    RestTemplate template = builder.build();
    return template;
  }
}

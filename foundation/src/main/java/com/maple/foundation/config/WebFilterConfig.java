package com.maple.foundation.config;

import com.maple.foundation.base.filter.AuthFilter;
import com.maple.foundation.base.filter.CorsFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qifeng.b.chen
 * @version 1.0
 * @date 2022/3/10-18:41
 * @since 1.8
 */
@Configuration
public class WebFilterConfig {
  @Bean
  public FilterRegistrationBean corsFilterRegistrationBean() {
    FilterRegistrationBean registrationBean = new FilterRegistrationBean(new CorsFilter());
    // 过滤所有路径
    registrationBean.addUrlPatterns("/*");
    // 设置过滤器名称
    registrationBean.setName("corsFilter");
    // 设置过滤器优先级 数值越低优先级越高
    registrationBean.setOrder(1);
    return registrationBean;
  }

  @Bean
  public FilterRegistrationBean authFilterRegistrationBean() {
    FilterRegistrationBean registrationBean = new FilterRegistrationBean(new AuthFilter());
    // 过滤所有路径
    registrationBean.addUrlPatterns("/*");
    // 添加不过滤路径
    registrationBean.addInitParameter("noFilter", "/user/login,/user/forgetPassword");
    // 设置过滤器名称
    registrationBean.setName("authFilter");
    // 设置过滤器优先级 数值越低优先级越高
    registrationBean.setOrder(2);
    return registrationBean;
  }
}

package com.maple.common.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author qifeng.b.chen
 * @version 1.0
 * @date 2022/3/22-18:26
 * @since 1.8
 */
@Slf4j
@Import({DataSourceAutoConfiguration.class})
@Configuration
public class AutoConfiguration {}

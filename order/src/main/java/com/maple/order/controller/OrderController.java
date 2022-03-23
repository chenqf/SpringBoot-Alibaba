package com.maple.order.controller;

import com.maple.order.entity.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qifeng.b.chen
 * @version 1.0
 * @date 2022/3/14-17:22
 * @since 1.8
 */
@RestController
@RequestMapping("order")
public class OrderController {
  @Value("${server.port}")
  private String port;

  @GetMapping
  public Object demo() {
    return new Order(100L, "天狗" + port);
  }
}

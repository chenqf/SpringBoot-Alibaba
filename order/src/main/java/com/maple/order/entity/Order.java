package com.maple.order.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qifeng.b.chen
 * @version 1.0
 * @date 2022/3/14-17:24
 * @since 1.8
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
  private Long id;
  private String name;
}

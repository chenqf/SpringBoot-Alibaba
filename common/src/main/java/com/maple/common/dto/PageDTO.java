package com.maple.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author qifeng.b.chen
 * @version 1.0
 * @date 2022/3/10-14:25
 * @since 1.8
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageDTO implements Serializable {

  private static final long serialVersionUID = -71145637487715282L;

  /** 当前页码(第几页) */
  private Integer pageNum;
  /** 每页多少条数据 */
  private Integer pageSize;
  /** 排序字段 */
  private String orderColumn;
  /** 排序规则 */
  private String orderRule;
}

package com.maple.foundation.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * (Role)实体类
 *
 * @author qifeng.b.chen
 * @since 2022-03-18 22:51:23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role implements Serializable {
  private static final long serialVersionUID = 735661216424452430L;
  /** 主键 */
  private Long id;
  /** 角色名 */
  private String name;
  /** 0:禁用;1启用 */
  private Integer status;
  /** 是否被删除 */
  private Boolean deleted;
  /** 创建人 */
  private Long createBy;
  /** 最后修改人 */
  private Long modifiedBy;
  /** 创建时间 */
  private Date gmtCreate;
  /** 最后修改时间 */
  private Date gmtModified;
}

package com.maple.foundation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * (User)DTO
 *
 * @author qifeng.b.chen
 * @since 2022-03-19 14:41:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO implements Serializable {
  private static final long serialVersionUID = 965997195599977035L;
  /** 主键 */
  private Long id;
  /** 用户名 */
  private String username;
  /** 密码 */
  private String password;
  /** 随机数用于生成密码 */
  private String random;
  /** 昵称 */
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

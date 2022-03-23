package com.maple.foundation.entity;

import com.maple.common.mybatis.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * (User)实体类
 *
 * @author qifeng.b.chen
 * @since 2022-03-09 16:40:23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@OptimisticLock(value = "version")
public class User implements Serializable {
  private static final long serialVersionUID = -71125977487715282L;
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
  @DefaultNumberValue(value = 0, column = "status")
  private Integer status;
  /** 是否被删除 */
  @DefaultBooleanValue(value = false, column = "is_deleted")
  private Boolean deleted;
  /** 创建人 */
  @CreatedBy private Long createBy;
  /** 最后修改人 */
  @LastModifiedBy private Long modifiedBy;
  /** 创建时间 */
  @CreatedDate private Date gmtCreate;
  /** 最后修改时间 */
  @LastModifiedDate private Date gmtModified;
}

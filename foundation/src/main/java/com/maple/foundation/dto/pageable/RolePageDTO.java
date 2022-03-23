package com.maple.foundation.dto.pageable;

import com.maple.common.dto.PageDTO;
import com.maple.foundation.dto.RoleDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qifeng.b.chen
 * @version 1.0
 * @date 2022/3/11-22:17
 * @since 1.8
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePageDTO {
  private PageDTO pageInfo;
  private RoleDTO queryInfo;
}

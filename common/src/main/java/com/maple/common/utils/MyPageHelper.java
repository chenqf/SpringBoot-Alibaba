package com.maple.common.utils;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.maple.common.dto.PageDTO;

/**
 * @author qifeng.b.chen
 * @version 1.0
 * @date 2022/3/10-14:34
 * @since 1.8
 */
public class MyPageHelper {
  public static <E> Page<E> startPage(PageDTO pageDTO) {
    int pageNum = pageDTO.getPageNum() == null ? 1 : pageDTO.getPageNum();
    int pageSize = pageDTO.getPageSize() == null ? 10 : pageDTO.getPageSize();
    String orderColumn = pageDTO.getOrderColumn() == null ? "id" : pageDTO.getOrderColumn();
    String orderRule = pageDTO.getOrderRule() == null ? "desc" : pageDTO.getOrderRule();
    String orderBy = orderColumn + " " + orderRule;
    Page<E> page = PageHelper.startPage(pageNum, pageSize);
    page.setOrderBy(orderBy);
    return page;
  }
}

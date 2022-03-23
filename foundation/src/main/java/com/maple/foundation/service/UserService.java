package com.maple.foundation.service;

import com.github.pagehelper.PageInfo;
import com.maple.foundation.dto.UserDTO;
import com.maple.foundation.dto.pageable.UserPageDTO;
import com.maple.foundation.entity.User;

import java.util.List;

/**
 * (User)表服务接口
 *
 * @author qifeng.b.chen
 * @since 2022-03-09 16:40:10
 */
public interface UserService {

  /**
   * 通过搜索条件和分页信息查询数据
   *
   * @param userPageDTO 分页及查询参数
   * @return 分页列表数据
   */
  PageInfo<User> queryPages(UserPageDTO userPageDTO);

  /**
   * 通过搜索条件和分页信息查询数据
   *
   * @param userDTO 查询参数
   * @return 分页列表数据
   */
  List<User> queryList(UserDTO userDTO);

  /**
   * 通过ID查询单条数据
   *
   * @param id 主键
   * @return 实例对象
   */
  UserDTO queryById(Long id);

  UserDTO queryByIdMaster(Long id);

  UserDTO queryByIdSlave(Long id);

  /**
   * 新增数据
   *
   * @param userDTO 实例对象
   * @return 实例对象
   */
  Long insert(UserDTO userDTO);

  /**
   * 修改数据
   *
   * @param userDTO 实例对象
   * @return 实例对象
   */
  void update(UserDTO userDTO);

  /**
   * 通过主键删除数据
   *
   * @param id 主键
   * @return 是否成功
   */
  void deleteById(Long id);
}

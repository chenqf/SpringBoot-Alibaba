package com.maple.foundation.service.impl;

import com.github.pagehelper.PageInfo;
import com.maple.common.datasource.RoutingWithMaster;
import com.maple.common.dto.PageDTO;
import com.maple.common.utils.BeanCopyUtil;
import com.maple.common.utils.MyPageHelper;
import com.maple.foundation.dto.UserDTO;
import com.maple.foundation.dto.pageable.UserPageDTO;
import com.maple.foundation.entity.User;
import com.maple.foundation.mapper.UserMapper;
import com.maple.foundation.service.UserService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * (User)表服务实现类
 *
 * @author qifeng.b.chen
 * @since 2022-03-09 16:51:01
 */
@Service("userService")
public class UserServiceImpl implements UserService {
  @Resource private UserMapper userMapper;

  /**
   * 通过搜索条件和分页信息查询数据
   *
   * @return 分页列表数据
   */
  @Override
  public PageInfo<User> queryPages(UserPageDTO userPageDTO) {
    PageDTO pageInfo = userPageDTO.getPageInfo();
    UserDTO queryInfo = userPageDTO.getQueryInfo();
    User user = new User();
    BeanCopyUtil.copyProperties(queryInfo, user);
    MyPageHelper.startPage(pageInfo);
    // TODO 缺少转换为DTO
    return PageInfo.of(this.userMapper.queryList(user));
  }

  /**
   * 通过搜索条件查询数据
   *
   * @return 分页列表数据
   */
  @Override
  public List<User> queryList(UserDTO userDTO) {
    User user = new User();
    // TODO 后面使用转换器实现
    BeanCopyUtil.copyProperties(userDTO, user);
    return this.userMapper.queryList(user);
  }

  /**
   * 通过ID查询单条数据
   *
   * @param id 主键
   * @return 实例对象
   */
  @Override
  @Cacheable(
      cacheNames = {"user"},
      key = "#id")
  public UserDTO queryById(Long id) {
    User user = this.userMapper.queryById(id);
    UserDTO userDTO = new UserDTO();
    BeanCopyUtil.copyProperties(user, userDTO);
    return userDTO;
  }

  @Override
  @RoutingWithMaster
  public UserDTO queryByIdMaster(Long id) {
    User user = this.userMapper.queryById(id);
    UserDTO userDTO = new UserDTO();
    BeanCopyUtil.copyProperties(user, userDTO);
    return userDTO;
  }

  @Override
  public UserDTO queryByIdSlave(Long id) {
    User user = this.userMapper.queryById(id);
    UserDTO userDTO = new UserDTO();
    BeanCopyUtil.copyProperties(user, userDTO);
    return userDTO;
  }

  /**
   * 新增数据
   *
   * @param userDTO 实例对象
   * @return 实例对象
   */
  @Override
  //  @Transactional(rollbackFor = Exception.class)
  public Long insert(UserDTO userDTO) {
    User user = new User();
    BeanCopyUtil.copyProperties(userDTO, user);
    this.userMapper.insert(user);
    return user.getId();
  }

  /**
   * 修改数据
   *
   * @param userDTO 实例对象
   */
  @Override
  public void update(UserDTO userDTO) {
    User user = new User();
    BeanCopyUtil.copyProperties(userDTO, user);
    // TODO 小于等于0 应抛出异常
    int num = this.userMapper.update(user);
  }

  /**
   * 通过主键删除数据
   *
   * @param id 主键
   */
  @Override
  public void deleteById(Long id) {
    int num = this.userMapper.deleteById(id);
    // TODO 小于等于0 应抛出异常
  }
}

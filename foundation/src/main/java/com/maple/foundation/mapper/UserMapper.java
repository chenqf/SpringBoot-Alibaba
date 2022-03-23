package com.maple.foundation.mapper;

import com.maple.foundation.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (User)表数据库访问层
 *
 * @author qifeng.b.chen
 * @since 2022-03-09 16:45:42
 */
// @Mapper 有包扫描，就不用它了
public interface UserMapper {

  /**
   * 通过ID查询单条数据
   *
   * @param id 主键
   * @return 实例对象
   */
  User queryById(Long id);

  /**
   * 通过检索数据查询多条数据
   *
   * @param user 查询条件
   * @return 多条数据
   */
  List<User> queryList(User user);

  /**
   * 新增数据
   *
   * @param user 实例对象
   * @return 影响行数
   */
  int insert(User user);

  /**
   * 批量新增数据（MyBatis原生foreach方法）
   *
   * @param entities List<User> 实例对象列表
   * @return 影响行数
   */
  int insertBatch(@Param("entities") List<User> entities);

  /**
   * 修改数据
   *
   * @param user 实例对象
   * @return 影响行数
   */
  int update(User user);

  /**
   * 通过主键删除数据
   *
   * @param id 主键
   * @return 影响行数
   */
  int deleteById(Long id);
}

package com.maple.foundation.controller;

import com.github.pagehelper.PageInfo;
import com.maple.foundation.dto.UserDTO;
import com.maple.foundation.dto.pageable.UserPageDTO;
import com.maple.foundation.entity.User;
import com.maple.foundation.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * (User)表控制层
 *
 * @author qifeng.b.chen
 * @since 2022-03-09 16:40:08
 */
@RestController
@RequestMapping("user")
public class UserController {
  /** 服务对象 */
  @Resource private UserService userService;

  /**
   * 通过搜索条件和分页信息查询数据
   *
   * @return 分页列表数据
   */
  @PostMapping("pages")
  public PageInfo<User> queryPages(@RequestBody UserPageDTO userPageDTO) {
    return this.userService.queryPages(userPageDTO);
  }

  /**
   * 通过搜索条件查询所有数据（不分页）
   *
   * @return 列表所有数据
   */
  @PostMapping("list")
  public List<User> queryList(@RequestBody UserDTO userDTO) {

    return this.userService.queryList(userDTO);
  }

  /**
   * 通过主键查询单条数据
   *
   * @param id 主键
   * @return 单条数据
   */
  @GetMapping("{id}")
  public UserDTO queryById(@PathVariable("id") Long id) {
    return this.userService.queryById(id);
  }

  @GetMapping("master/{id}")
  public UserDTO queryByIdMaster(@PathVariable("id") Long id) {
    return this.userService.queryByIdMaster(id);
  }

  @GetMapping("slave/{id}")
  public UserDTO queryByIdSlave(@PathVariable("id") Long id) {
    return this.userService.queryByIdSlave(id);
  }

  /**
   * 新增数据
   *
   * @param userDTO 实体
   * @return 新增结果
   */
  @PostMapping
  public Long insert(@RequestBody UserDTO userDTO) {
    return this.userService.insert(userDTO);
  }

  /**
   * 编辑数据
   *
   * @param userDTO 实体
   */
  @PutMapping
  public void update(@RequestBody UserDTO userDTO) {
    this.userService.update(userDTO);
  }

  /**
   * 删除数据
   *
   * @param id 主键
   * @return 删除是否成功
   */
  @DeleteMapping("{id}")
  public void deleteById(@PathVariable("id") Long id) {
    this.userService.deleteById(id);
  }
}

package com.maple.foundation;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author qifeng.b.chen
 * @version 1.0
 * @date 2022/3/7-13:02
 * @since 1.8
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Rollback(false)
public class BaseTests {
  @Before
  public void init() {
    System.out.println("开始测试-----------------");
  }

  @After
  public void after() {
    System.out.println("测试结束-----------------");
  }
}

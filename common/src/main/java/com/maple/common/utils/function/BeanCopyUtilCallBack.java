package com.maple.common.utils.function;

/**
 * @author qifeng.b.chen
 * @version 1.0
 * @date 2022/3/8-13:22
 * @since 1.8
 */
@FunctionalInterface
public interface BeanCopyUtilCallBack<S, T> {
  void callBack(S t, T s);
}

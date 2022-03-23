package com.maple.common.utils;

import com.maple.common.utils.function.BeanCopyUtilCallBack;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author qifeng.b.chen
 * @version 1.0
 * @date 2022/3/8-13:21
 * @since 1.8
 */
public class BeanCopyUtil extends BeanUtils {
  public static <S, T> List<T> copyListProperties(List<S> sources, Supplier<T> target) {
    return copyListProperties(sources, target, null);
  }

  public static <S, T> List<T> copyListProperties(
      List<S> sources, Supplier<T> target, BeanCopyUtilCallBack<S, T> callBack) {
    List<T> list = new ArrayList<>(sources.size());
    for (S source : sources) {
      T t = target.get();
      copyProperties(source, t);
      list.add(t);
      if (callBack != null) {
        // 回调
        callBack.callBack(source, t);
      }
    }
    return list;
  }
}

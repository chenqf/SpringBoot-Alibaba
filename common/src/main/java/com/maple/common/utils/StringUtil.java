package com.maple.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author qifeng.b.chen
 * @version 1.0
 * @date 2022/3/20-14:30
 * @since 1.8
 */
public class StringUtil {
  private static Pattern linePattern = Pattern.compile("_(\\w)");
  /** 下划线转驼峰 */
  public static String lineToHump(String str) {
    str = str.toLowerCase();
    Matcher matcher = linePattern.matcher(str);
    StringBuffer sb = new StringBuffer();
    while (matcher.find()) {
      matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
    }
    matcher.appendTail(sb);
    return sb.toString();
  }

  /** 驼峰转下划线 */
  public static String humpToLine(String str) {
    return str.replaceAll("[A-Z]", "_$0").toLowerCase();
  }
}

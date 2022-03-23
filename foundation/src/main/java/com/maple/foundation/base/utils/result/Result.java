package com.maple.foundation.base.utils.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qifeng.b.chen
 * @version 1.0
 * @date 2022/1/24-15:11
 * @since 1.8
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
  private int code;
  private String message;
  private Object data;
  private long timestamp;

  public Result(int code, String message, Object data) {
    this.code = code;
    this.message = message;
    this.data = data;
    this.timestamp = System.currentTimeMillis();
  }

  public Result(int code, String message) {
    this.code = code;
    this.message = message;
    this.timestamp = System.currentTimeMillis();
  }
}

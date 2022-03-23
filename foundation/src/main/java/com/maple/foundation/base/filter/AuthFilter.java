package com.maple.foundation.base.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author qifeng.b.chen
 * @version 1.0
 * @date 2022/3/10-15:10
 * @since 1.8
 */
@Slf4j
public class AuthFilter implements Filter {

  /** 不过滤的资源 */
  private String[] noFilter;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    // 将不过滤的资源存入数组中
    String noFilterString = filterConfig.getInitParameter("noFilter");
    if (noFilterString != null && noFilterString.length() > 0) {
      noFilter = noFilterString.split(",");
    }
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    String requestURI = request.getRequestURI();
    String method = request.getMethod();
    boolean flag = isNoFilter(requestURI, method);
    log.info("auth-filter: [ " + requestURI + " / " + method + " ] result: [ " + flag + " ]");
    req.setAttribute("userId", 150L);
    chain.doFilter(req, res);
  }

  @Override
  public void destroy() {}

  public boolean isNoFilter(String requestURI, String method) {
    for (String s : noFilter) {
      if (requestURI.contains(s)) {
        return true;
      }
    }
    return false;
  }
}

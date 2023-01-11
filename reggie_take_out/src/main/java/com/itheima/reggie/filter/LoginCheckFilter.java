package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 作者：Nega Nebulus
 * 时间：2023/1/6 15:03
 * 描述：检查当前用户是否完成登录的过滤器
 */
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符的写法
    public static final AntPathMatcher PATH_MATCHER =new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1 获取本次登录请求的URI
        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}",requestURI);

        //定义不需要处理的请求路径（白名单）
        String[] urls = new String[]{
                "/employee/login",//正常登录请求放行
                "/employee/logout",//正常登出请求放行
                "/backend/**",//静态资源予以放行
                "/front/**",//移动端静态资源予以放行
                "/common/**"
        };

        //2 判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        //3 如不需处理则直接放行
        if(check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        //4 判断登录状态，如已登录，直接放行
        if(request.getSession().getAttribute("employee") != null){
            log.info("用户已登录，用户ID为{}",request.getSession().getAttribute("employee"));
            //登录后通过session获取到当前用户的登录ID
            Long empId = (Long) request.getSession().getAttribute("employee");
            //调用BaseContext类中的set方法，将当前用户ID保存到ThreadLocal线程中，用于后续操作中的使用
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request, response);
            return;
        }
        log.info("用户未登录");
        //5 如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

        return;
    }

    /**
     * 路径匹配，检查当前请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
        public boolean check(String[] urls,String requestURI) {
            for (String url : urls) {
                boolean match = PATH_MATCHER.match(url, requestURI);
                if (match) {
                    return true;
                }
            }
            return false;
        }

}

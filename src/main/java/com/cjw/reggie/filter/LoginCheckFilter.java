package com.cjw.reggie.filter;


import com.alibaba.fastjson.JSON;
import com.cjw.reggie.common.BaseContext;
import com.cjw.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1、获取本次请求的URI
        String requestUrl = request.getRequestURI();


        //定义不需要处理的请求路径

        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login",
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"
        };

        //2、判断本次请求是否需要处理
        boolean flag = checkUrlMatch(urls,requestUrl);


        //3、如果不需要处理，则直接放行

        if (flag){
            //log.info("本次请求不需要处理:"+requestUrl);
            filterChain.doFilter(request,response);
            return;
        }

        //4-1、判断登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("employee") != null){
            //log.info("已登录,用户id为："+request.getSession().getId());

            Long empId  = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        }

        //4-2、判断登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("user") != null){
            //log.info("已登录,用户id为："+request.getSession().getId());

            Long userId  = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);
            return;
        }


        //5、如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
        //log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;



    }

    /**
     * 匹配url是否处理
     * @param urls
     * @param requestUrl
     * @return
     */
    public boolean checkUrlMatch(String[] urls,String requestUrl){
        for (String url: urls) {
            boolean match = PATH_MATCHER.match(url,requestUrl);
            if (match){
                return true;
            }
        }
        return false;
    }
}

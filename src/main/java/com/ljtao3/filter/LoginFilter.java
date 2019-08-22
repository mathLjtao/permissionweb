package com.ljtao3.filter;

import com.ljtao3.common.MyRequestHolder;
import com.ljtao3.model.SysUser;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Slf4j
public class LoginFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest)servletRequest;
        HttpServletResponse response=(HttpServletResponse) servletResponse;
        String servletPath=request.getServletPath();
        SysUser user =(SysUser) request.getSession().getAttribute("user");
        if(user ==null){
            String path="/signin.jsp";
            response.sendRedirect(path);
            return ;
        }
        MyRequestHolder.add(user);
        MyRequestHolder.add(request);
        filterChain.doFilter(request,response);
    }

    @Override
    public void destroy() {

    }
}

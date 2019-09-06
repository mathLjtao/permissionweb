package com.ljtao3.filter;

import com.ljtao3.common.MyRequestHolder;
import com.ljtao3.dto.LoginUser;
import com.ljtao3.model.SysUser;
import com.ljtao3.util.IpUtil;
import com.ljtao3.util.LoginUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

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
        /*
        以前从session获取用户的例子

        SysUser user =(SysUser) request.getSession().getAttribute("user");

        if(user ==null){
            String path="/signin.jsp";
            //做测试的时候这里先关掉
            response.sendRedirect(path);
            return ;
        }
        */
        /*
        现在在cookie中获取用户的例子
         */
        String ip = IpUtil.getUserIP(request);

        LoginUser loginUser = LoginUtil.getUserFromCookie(request, response);
        if (loginUser == null || !loginUser.isRet() || loginUser.getUser() == null) {
            String ret = request.getRequestURI();
            String parameterString = request.getQueryString();
            if (StringUtils.isNotBlank(parameterString)) {
                ret += "?" + parameterString;
            }
            log.info("cannot visit {}, param:{}, ip:{}, not login", servletPath, parameterString, ip);
            response.sendRedirect("/signin.jsp?ret=" + URLEncoder.encode(ret));
            return;
        }


        //做测试的时候，这里先开启来
//        user=new SysUser();
//        user.setUsername("ljtao3");

        MyRequestHolder.add(loginUser.getUser());
        MyRequestHolder.add(request);
        filterChain.doFilter(request,response);
    }
    @Override
    public void destroy() {

    }
}

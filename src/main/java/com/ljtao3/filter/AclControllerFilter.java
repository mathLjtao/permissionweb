package com.ljtao3.filter;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.ljtao3.common.MyRequestHolder;
import com.ljtao3.model.SysUser;
import com.ljtao3.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
/*
    权限拦截器
 */
@Slf4j
public class AclControllerFilter implements Filter {
    private static Set<String> exclusionUrlSet= Sets.newConcurrentHashSet();
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String exclusionUrls = filterConfig.getInitParameter("exclusionUrls");
        List<String> exclusionUrlList = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(exclusionUrls);
        exclusionUrlSet=Sets.newConcurrentHashSet(exclusionUrlList);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest)servletRequest;
        HttpServletResponse response=(HttpServletResponse)servletResponse;
        String servletPath=request.getServletPath();
        Map requestMap=request.getParameterMap();
        if(exclusionUrlSet.contains(servletPath)){
            filterChain.doFilter(request,response);
            return ;
        }
        SysUser sysUser= MyRequestHolder.getCurrentUser();
        if(sysUser==null){
            log.info("someone visit {},but no login ,parameter :{} ",servletPath, JsonMapper.obj2String(requestMap));
            noAuth(request,response);
            return;
        }
    }

    private void noAuth(HttpServletRequest request, HttpServletResponse response) {

    }

    @Override
    public void destroy() {

    }
}

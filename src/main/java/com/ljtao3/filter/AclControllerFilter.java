package com.ljtao3.filter;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.ljtao3.common.ApplicationContextHelper;
import com.ljtao3.common.JsonData;
import com.ljtao3.common.MyRequestHolder;
import com.ljtao3.model.SysUser;
import com.ljtao3.service.SysCoreService;
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
    //无权限访问跳转URl
    private final static String noAuthUrl="/sys/user/noAuth.page";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //不用拦截的url
        String exclusionUrls = filterConfig.getInitParameter("exclusionUrls");
        List<String> exclusionUrlList = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(exclusionUrls);
        exclusionUrlSet=Sets.newConcurrentHashSet(exclusionUrlList);
        //exclusionUrlList.add(noAuthUrl);//用exclusionUrlList添加会报错，因为这个是Arrays的内部类ArrayList，不能用add等方法
        exclusionUrlSet.add(noAuthUrl);
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
        //判断是否已登陆
        if(sysUser==null){
            log.info("someone visit {},but no login ,parameter :{} ",servletPath, JsonMapper.obj2String(requestMap));
            noAuth(request,response);
            return;
        }
        SysCoreService sysCoreService= ApplicationContextHelper.popBean(SysCoreService.class);
        //判断有没有权限访问链接
        if(!sysCoreService.hasUrlAcl(servletPath)){
            log.info("{} visit {} ,but no auth ,parameter :{}",JsonMapper.obj2String(sysUser),servletPath, JsonMapper.obj2String(requestMap));
            noAuth(request,response);
            return;
        }
        filterChain.doFilter(servletRequest,servletResponse);
        return ;
    }
    //无权限跳转
    private void noAuth(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String servletPath = request.getServletPath();
        if(servletPath.endsWith(".json")){
            JsonData jsonData=JsonData.fail("没有访问权限，请联系管理员！");
            response.setHeader("Content-Type","application/json");
            response.getWriter().print(JsonMapper.obj2String(jsonData));
            return;
        }else{
            clientRedirect(noAuthUrl,response);
            return;
        }
    }
    private void clientRedirect(String url, HttpServletResponse response) throws IOException{
        response.setHeader("Content-Type", "text/html");
        response.getWriter().print("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
                + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + "<head>\n" + "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"/>\n"
                + "<title>跳转中...</title>\n" + "</head>\n" + "<body>\n" + "跳转中，请稍候...\n" + "<script type=\"text/javascript\">//<![CDATA[\n"
                + "window.location.href='" + url + "?ret='+encodeURIComponent(window.location.href);\n" + "//]]></script>\n" + "</body>\n" + "</html>\n");
    }

    @Override
    public void destroy() {

    }
}

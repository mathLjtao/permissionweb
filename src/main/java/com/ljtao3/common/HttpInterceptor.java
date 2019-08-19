package com.ljtao3.common;

import com.ljtao3.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/*
Http请求前后处理
 */
@Slf4j
public class HttpInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url=request.getRequestURI().toString();
        Map parameterMap =  request.getParameterMap();
        //log.info("request start url:{},param:{}",url, JsonMapper.obj2String(parameterMap));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String url=request.getRequestURI().toString();
        Map parameterMap =  request.getParameterMap();
        //log.info("request finished url:{},param:{}",url, JsonMapper.obj2String(parameterMap));

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String url=request.getRequestURI().toString();
        Map parameterMap =  request.getParameterMap();
        log.info("request exception url:{},param:{}",url, JsonMapper.obj2String(parameterMap));

    }
}

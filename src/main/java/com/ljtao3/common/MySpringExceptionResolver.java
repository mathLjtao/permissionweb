package com.ljtao3.common;

import com.ljtao3.exception.MyPermissionException;
import com.ljtao3.exception.ParamException;
import com.mysql.fabric.xmlrpc.base.Param;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
接口请求全局异常处理
 */
@Slf4j
public class MySpringExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object o, Exception ex) {
        String url=request.getRequestURI().toString();
        ModelAndView mv;
        String defaultMsg="System error";

        //.json , .page
        //设置了下面这些之后，要求项目所有请求json数据，都使用.json结果；所有请求paga页面，都使用.page结尾
        //才能运用到下面这些
        if (url.endsWith(".json")){
            if(ex instanceof MyPermissionException || ex instanceof ParamException){
                JsonData result= JsonData.fail(ex.getMessage());
                //"jsonView" 在spring-servlet.xml中配置。。。已json数据页面的形式返回
                mv=new ModelAndView("jsonView",result.toMap());
            }
            else{
                log.error("unknown json exception ,url:" + url,ex);
                JsonData result= JsonData.fail(defaultMsg);
                mv=new ModelAndView("jsonView",result.toMap());
            }
        }else if(url.endsWith(".page")){
            log.error("unknown page exception ,url:" + url,ex);
            JsonData result=JsonData.fail(defaultMsg);
            mv=new ModelAndView("exception",result.toMap());
        }
        else{
            log.error("unknown  exception ,url:" + url,ex);
            JsonData result=JsonData.fail(defaultMsg);
            mv=new ModelAndView("jsonView",result.toMap());
        }
        return mv;
    }
}

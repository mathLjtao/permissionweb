package com.ljtao3.controller;

import com.ljtao3.common.ApplicationContextHelper;
import com.ljtao3.common.JsonData;
import com.ljtao3.dao.SysAclModuleMapper;
import com.ljtao3.exception.MyPermissionException;
import com.ljtao3.model.SysAclModule;
import com.ljtao3.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
@Slf4j
public class TestController {
    @RequestMapping("/hello")
    //将返回数据以json的形式，通过response响应返回到页面，
    @ResponseBody
    public String hello(){
        log.info("hello");
        return "hello world !!";
    }
    @RequestMapping("/login")
    public String toLogingJsp(){
        return "login";
    }

    //运用下自己写的JsonData类
    @RequestMapping("/hello.json")
    @ResponseBody
    public JsonData JsonData_hello(){
        return JsonData.success("JsonData_hello() !! ");
    }

    //运用下自己写的JsonData类 ，再加上抛出异常
    @RequestMapping("/helloAndException.json")
    @ResponseBody
    public JsonData JsonData_hello_exception(){
        //throw new MyPermissionException("test Exception !! ");
        throw new RuntimeException("test Exception !! ");
    }
    //page页面信息错误
    @RequestMapping("/page_exception.page")
    public String page_exception(){
        //throw new MyPermissionException("test Exception !! ");
        int i=1/0;
        return "login";
    }
    @RequestMapping("/exception1")
    public String exception1(){
        //throw new MyPermissionException("test Exception !! ");
        int i=1/0;
        return "login";
    }
    //测试获取ApplicationContext上下文工具，JsonMapper工具，
    @RequestMapping("/test1.json")
    @ResponseBody
    public JsonData test1() {
        log.info("test1");
        //这里从上下文获取了 SysAclModuleMapper 对象
        SysAclModuleMapper moduleMapper= ApplicationContextHelper.popBean(SysAclModuleMapper.class);
        SysAclModule sysAclModule=moduleMapper.selectByPrimaryKey(1);
        log.info(JsonMapper.obj2String(sysAclModule));
        return JsonData.success("test1 success!!");
    }

}

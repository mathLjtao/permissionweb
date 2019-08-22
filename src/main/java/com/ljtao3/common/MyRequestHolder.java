package com.ljtao3.common;

import com.ljtao3.model.SysUser;

import javax.servlet.http.HttpServletRequest;
/*
在filter.LoginFilter中有用到add()方法
 */
public class MyRequestHolder {
    //static final 修饰的，不能更换成其他的ThreadLocal对象，但是可以对当前赋值的ThreadLocal对象进行设置
    private static final ThreadLocal<SysUser> userHolder=new ThreadLocal<>();
    private static final ThreadLocal<HttpServletRequest> requestHolder=new ThreadLocal<>();
    public static void add(SysUser sysUser){
        userHolder.set(sysUser);
    }
    public static SysUser getCurrentUser(){
        return userHolder.get();
    }
    public static void add(HttpServletRequest request){
        requestHolder.set(request);
    }
    public static HttpServletRequest getCurrentRequest(){
        return requestHolder.get();
    }
    public static void remove(){
        userHolder.remove();
        requestHolder.remove();
    }
}

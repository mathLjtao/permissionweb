package com.ljtao3.controller;

import com.ljtao3.model.SysUser;
import com.ljtao3.service.SysUserService;
import com.ljtao3.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ExecutionException;

/*
用户登录模块
 */
@Controller
@RequestMapping("")
public class UserController {
    @Resource
    private SysUserService sysUserService;
    @RequestMapping("/logout.page")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.getSession().invalidate();
        String path="/signin.jsp";
        response.sendRedirect(path);
    }
    @RequestMapping("/login.page")
    public void login(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //在页面的username --》 telephone  或者是 mail
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        SysUser sysUser = sysUserService.findByKeyword(username);
        String errorMsg="";
        String ret=request.getParameter("ret");
        if(StringUtils.isBlank(username)){
            errorMsg="用户名不能为空！";
        }else if(StringUtils.isBlank(password)){
            errorMsg="密码不能为空！";
        }else if(sysUser==null){
            errorMsg="用户不存在！";
        }else if(!sysUser.getPassword().equals(MD5Util.encrypt(password))){
            errorMsg="用户名或密码错误 ！";
        }else if(sysUser.getStatus()!=1){
            errorMsg="用户被冻结，请联系管理员！";
        }
        else{
            //login success
            request.getSession().setAttribute("user",sysUser);
            if(StringUtils.isNotEmpty(ret)){
                response.sendRedirect(ret);
            }else{
                response.sendRedirect("/admin/index.page");//todo
            }
        }
        request.setAttribute("error",errorMsg);
        request.setAttribute("username",username);
        if(StringUtils.isNotEmpty(ret)){
            request.setAttribute("ret",ret);
        }
        String path="/signin.jsp";
        request.getRequestDispatcher(path).forward(request,response);
    }
}

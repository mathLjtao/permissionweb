package com.ljtao3.controller;

import com.ljtao3.model.SysUser;
import com.ljtao3.service.SysUserService;
import com.ljtao3.util.IpUtil;
import com.ljtao3.util.LoginUtil;
import com.ljtao3.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ExecutionException;

/*
用户登录模块
 */
@Slf4j
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
        String ip = IpUtil.getUserIP(request);
//        username="18826078745";
//        password="123456";
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
            log.info("login succeed, ip:{}, username:{}", ip, username);
            //login success

            //request.getSession().setAttribute("user",sysUser);//以前用户信息保存在session
            LoginUtil.saveUserToCookie(request, response, sysUser);

            if(StringUtils.isNotEmpty(ret)){
                response.sendRedirect(ret);
            }else{
                response.sendRedirect("/admin/index.page");//todo
            }
            return ;
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

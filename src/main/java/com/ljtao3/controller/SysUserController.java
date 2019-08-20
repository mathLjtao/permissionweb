package com.ljtao3.controller;

import com.ljtao3.beans.PageQuery;
import com.ljtao3.beans.PageResult;
import com.ljtao3.common.JsonData;
import com.ljtao3.model.SysUser;
import com.ljtao3.param.UserParam;
import com.ljtao3.service.SysUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
/*
管理用户模块
 */
@Controller
@RequestMapping("/sys/user")
public class SysUserController {
    @Resource
    private SysUserService sysUserService;
    @RequestMapping("/page.json")
    @ResponseBody
    public JsonData userPage(Integer deptId, PageQuery pageQuery){
        PageResult<SysUser> result = sysUserService.getPageByDeptId(deptId, pageQuery);
        return JsonData.success(result);
    }
    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData saveUser(UserParam param){
        sysUserService.save(param);
        return  JsonData.success();
    }
    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateUser(UserParam param){
        sysUserService.update(param);
        return JsonData.success();
    }

}

package com.ljtao3.controller;

import com.google.common.collect.Maps;
import com.ljtao3.beans.PageQuery;
import com.ljtao3.beans.PageResult;
import com.ljtao3.common.JsonData;
import com.ljtao3.model.SysAcl;
import com.ljtao3.model.SysRole;
import com.ljtao3.param.AclParam;
import com.ljtao3.service.SysAclService;
import com.ljtao3.service.SysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/sys/acl")
@Slf4j
public class SysAclController {
    @Resource
    private SysAclService sysAclService;
    @Resource
    private SysRoleService  sysRoleService;
    @RequestMapping("/page.json")
    @ResponseBody
    public JsonData aclPage(Integer aclModuleId, PageQuery pageQuery){
        PageResult<SysAcl> pageByAclModuleId = sysAclService.getPageByAclModuleId(aclModuleId,pageQuery);
        return JsonData.success(pageByAclModuleId);
    }
    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData save(AclParam param){
        sysAclService.save(param);
        return JsonData.success();
    }
    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData update(AclParam param){
        sysAclService.update(param);
        return JsonData.success();
    }
    /*
    根据权限点id，获取哪些用户、角色拥有这个权限点
     */
    @RequestMapping("/acls.json")
    @ResponseBody
    public JsonData getAclByAclId(Integer aclId){
        Map<String,Object> map= Maps.newHashMap();
        List<SysRole> roleList = sysRoleService.getRoleListByAclId(aclId);
        map.put("users",sysRoleService.getUserListByRoleList(roleList));
        map.put("roles",roleList);
        return JsonData.success(map);
    }
}

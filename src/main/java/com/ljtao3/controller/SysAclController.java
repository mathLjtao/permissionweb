package com.ljtao3.controller;

import com.ljtao3.beans.PageQuery;
import com.ljtao3.beans.PageResult;
import com.ljtao3.common.JsonData;
import com.ljtao3.model.SysAcl;
import com.ljtao3.param.AclParam;
import com.ljtao3.service.SysAclService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("/sys/acl")
@Slf4j
public class SysAclController {
    @Resource
    private SysAclService sysAclService;
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
}

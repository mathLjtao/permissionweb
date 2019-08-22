package com.ljtao3.controller;

import com.ljtao3.common.JsonData;
import com.ljtao3.param.AclModuleParam;
import com.ljtao3.service.SysAclModuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

@Controller
@RequestMapping("/sys/aclModule")
@Slf4j
public class SysAclModuleController {
    @Resource
    private SysAclModuleService sysAclModuleService;
    @RequestMapping("/acl.page")
    public ModelAndView page(){
        return new ModelAndView("acl");
    }
    @RequestMapping("save.json")
    @ResponseBody
    public JsonData saveAclModule(AclModuleParam param){
        sysAclModuleService.save(param);
        return JsonData.success();
    }
    @RequestMapping("update.json")
    @ResponseBody
    public JsonData updateAclModule(AclModuleParam param){
        sysAclModuleService.update(param);
        return JsonData.success();
    }
}

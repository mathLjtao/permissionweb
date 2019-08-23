package com.ljtao3.controller;

import com.ljtao3.common.JsonData;
import com.ljtao3.dto.AclModuleLevelDto;
import com.ljtao3.model.SysRole;
import com.ljtao3.param.RoleParam;
import com.ljtao3.service.SysCoreService;
import com.ljtao3.service.SysRoleService;
import com.ljtao3.service.SysTreeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/sys/role")
public class SysRoleController {
    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private SysCoreService sysCoreService;
    @Resource
    private SysTreeService sysTreeService;
    @RequestMapping("/role.page")
    public ModelAndView rolePage(){
        return new ModelAndView("role");
    }
    @RequestMapping("/list.json")
    @ResponseBody
    public JsonData roleList(){
        List<SysRole> list=sysRoleService.roleList();
        return JsonData.success(list);
    }
    @RequestMapping("/roleTree.json")
    @ResponseBody
    public JsonData roleTree(Integer roleId){
        List<AclModuleLevelDto> aclModuleLevelDtos = sysTreeService.roleTree(roleId);
        return JsonData.success(aclModuleLevelDtos);
    }
    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData save(RoleParam param){
        sysRoleService.save(param);
        return JsonData.success();
    }
    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData update(RoleParam param){
        sysRoleService.update(param);
        return JsonData.success();
    }

}

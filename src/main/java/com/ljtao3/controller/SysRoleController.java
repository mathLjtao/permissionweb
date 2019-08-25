package com.ljtao3.controller;

import com.ljtao3.common.JsonData;
import com.ljtao3.dto.AclModuleLevelDto;
import com.ljtao3.model.SysRole;
import com.ljtao3.param.RoleParam;
import com.ljtao3.service.SysCoreService;
import com.ljtao3.service.SysRoleAclService;
import com.ljtao3.service.SysRoleService;
import com.ljtao3.service.SysTreeService;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @Resource
    private SysRoleAclService sysRoleAclService;
    @RequestMapping("/role.page")
    public ModelAndView rolePage(){
        return new ModelAndView("role");
    }
    /*
    展现所有角色出来
     */
    @RequestMapping("/list.json")
    @ResponseBody
    public JsonData roleList(){
        List<SysRole> list=sysRoleService.roleList();
        return JsonData.success(list);
    }
    /*
    展示角色与权限点的关系树
     */
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
    /*
        改变角色拥有的权限点
        如果不加上这个 @RequestParam(value="aclIds")
        会报这个异常 java.lang.NoSuchMethodException: java.util.List.<init>(
     */
    @RequestMapping("/changeAcls.json")
    @ResponseBody
    public JsonData changeAcls(@RequestParam(value="roleId") Integer roleId,@RequestParam(value="aclIds") List<Integer> aclIds){
        sysRoleAclService.changeAcls( roleId, aclIds);
        return JsonData.success();
    }

    /*
    根据权限加载出，是被哪些用户所拥有
     */
    @RequestMapping("users.page")
    @ResponseBody
    public JsonData getUsersByRoleId(Integer roleId){
        return  JsonData.success();
    }


}

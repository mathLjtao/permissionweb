package com.ljtao3.controller;

import com.google.common.collect.Lists;
import com.ljtao3.common.JsonData;
import com.ljtao3.dto.AclModuleLevelDto;
import com.ljtao3.model.SysRole;
import com.ljtao3.model.SysUser;
import com.ljtao3.param.RoleParam;
import com.ljtao3.service.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    @Resource
    private SysRoleUserService sysRoleUserService;
    @Resource
    private SysUserService sysUserService;
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
        //
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
    @RequestMapping("users.json")
    @ResponseBody
    public JsonData getUsersByRoleId(Integer roleId){
        List<SysUser> selectedUserList = sysRoleUserService.getListByRoleId(roleId);
        List<SysUser> allUserList = sysUserService.getAll();
        List<SysUser> unSelectedUserList= Lists.newArrayList();
        /*
        无存储。stream不是一种数据结构，它只是某种数据源的一个视图，数据源可以是一个数组，Java容器或I/O channel等。
为函数式编程而生。对stream的任何修改都不会修改背后的数据源，比如对stream执行过滤操作并不会删除被过滤的元素，而是会产生一个不包含被过滤元素的新stream。
惰式执行。stream上的操作并不会立即执行，只有等到用户真正需要结果的时候才会执行。
可消费性。stream只能被“消费”一次，一旦遍历过就会失效，就像容器的迭代器那样，想要再次遍历必须重新生成。
stream().map()方法的使用示例:
         */
        Set<Integer> selectedUserIdSet=selectedUserList.stream().map(sysUser -> sysUser.getId()).collect(Collectors.toSet());
        for(SysUser user:allUserList){
            if(user.getStatus()==1 && !selectedUserIdSet.contains(user.getId())){
                unSelectedUserList.add(user);
            }
        }
        //selectedUserList=selectedUserList.stream().filter(sysUser -> sysUser.getStatus()!=1).collect(Collectors.toList());
        Map<String,List<SysUser>> map=new HashMap<>();
        map.put("selected",selectedUserList);
        map.put("unselected",unSelectedUserList);
        return  JsonData.success(map);
    }
    @RequestMapping("/changeUsers.json")
    @ResponseBody
    public JsonData changeUsers(@RequestParam(value="roleId") Integer roleId,@RequestParam("userIds") Set<Integer> userIds){
        sysRoleUserService.changeUsers(roleId,userIds);
        return JsonData.success();
    }

}

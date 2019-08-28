package com.ljtao3.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.ljtao3.common.MyRequestHolder;
import com.ljtao3.dao.SysRoleAclMapper;
import com.ljtao3.dao.SysRoleMapper;
import com.ljtao3.dao.SysRoleUserMapper;
import com.ljtao3.dao.SysUserMapper;
import com.ljtao3.exception.ParamException;
import com.ljtao3.model.SysRole;
import com.ljtao3.model.SysUser;
import com.ljtao3.param.RoleParam;
import com.ljtao3.util.IpUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysRoleService {
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private SysRoleUserMapper sysRoleUserMapper;
    @Resource
    private SysRoleAclMapper sysRoleAclMapper;
    @Resource
    private SysUserMapper sysUserMapper;

    public void save(RoleParam param) {

        if(checkExist(param.getId(),param.getName())){
            throw new ParamException("存在相同名称管理员！");
        }
        SysRole sysRole=SysRole.builder().name(param.getName()).type(param.getType()).status(param.getStatus())
                .remark(param.getRemark()).build();
        sysRole.setOperator(MyRequestHolder.getCurrentUser().getUsername());
        sysRole.setOperateIp(IpUtil.getRemoteIp(MyRequestHolder.getCurrentRequest()));
        sysRole.setOperateTime(new Date());
        sysRoleMapper.insertSelective(sysRole);
    }

    public void update(RoleParam param) {
        if(checkExist(param.getId(),param.getName())){
            throw new ParamException("存在相同名称管理员！");
        }

        SysRole before=sysRoleMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新角色不存在");

        SysRole sysRole=SysRole.builder().id(param.getId()).name(param.getName()).type(param.getType()).status(param.getStatus())
                .remark(param.getRemark()).build();
        sysRole.setOperator(MyRequestHolder.getCurrentUser().getUsername());
        sysRole.setOperateIp(IpUtil.getRemoteIp(MyRequestHolder.getCurrentRequest()));
        sysRole.setOperateTime(new Date());
        sysRoleMapper.updateByPrimaryKeySelective(sysRole);
    }

    public List<SysRole> roleList() {

        return sysRoleMapper.getAllRole();
    }
    public boolean checkExist(Integer id,String name){

        return sysRoleMapper.countByName(id,name)>0;
    }

    public List<SysRole> getRoleListByUserId(int userId){
        List<Integer> roleIdList = sysRoleUserMapper.getRoleIdListByUserId(userId);
        if(CollectionUtils.isEmpty(roleIdList))
            return Lists.newArrayList();
        return sysRoleMapper.getByIdList(roleIdList);
    }
    public List<SysRole> getRoleListByAclId(int aclId){
        List<Integer> roleIdList=sysRoleAclMapper.getRoleIdListByAclId(aclId);
        if (CollectionUtils.isEmpty(roleIdList))
            return Lists.newArrayList();
        return sysRoleMapper.getByIdList(roleIdList);
    }

    public List<SysUser> getUserListByRoleList(List<SysRole> roleList){
        if(CollectionUtils.isEmpty(roleList))
            return Lists.newArrayList();
        List<Integer> roleIdList = roleList.stream().map(role -> role.getId()).collect(Collectors.toList());
        List<Integer> userIdList=sysRoleUserMapper.getUserIdListByRoleIdList(roleIdList);
        if(CollectionUtils.isEmpty(userIdList))
            return Lists.newArrayList();
        return sysUserMapper.getByIdList(userIdList);
    }
}

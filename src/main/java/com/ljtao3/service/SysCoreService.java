package com.ljtao3.service;

import com.google.common.collect.Lists;
import com.ljtao3.common.MyRequestHolder;
import com.ljtao3.dao.SysAclMapper;
import com.ljtao3.dao.SysRoleAclMapper;
import com.ljtao3.dao.SysRoleUserMapper;
import com.ljtao3.model.SysAcl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SysCoreService {
    @Resource
    private SysAclMapper sysAclMapper;
    @Resource
    private SysRoleUserMapper sysRoleUserMapper;
    @Resource
    private SysRoleAclMapper sysRoleAclMapper;
    public List<SysAcl> getCurrentUserAclList(){
        //int userId= MyRequestHolder.getCurrentUser().getId();
        int userId=9;
        return getUserAclList(userId);
    }

    public List<SysAcl> getRoleAclList(int roleId){
        List<Integer> userAclIdList=sysRoleAclMapper.getAclIdListByRoleId(roleId);
        if(CollectionUtils.isEmpty(userAclIdList)){
            return Lists.newArrayList();
        }
        return sysAclMapper.getByIdList(userAclIdList);
    }
    public List<SysAcl> getUserAclList(int userId){
        if(isSuperAdmin()){
            return sysAclMapper.getAllAcl();
        }
        List<Integer> userRoleIdList=sysRoleUserMapper.getRoleIdListByUserId(userId);
        if(CollectionUtils.isEmpty(userRoleIdList)){
            return Lists.newArrayList();
        }
        List<Integer> userAclIdList=sysRoleAclMapper.getAclIdListByRoleIdList(userRoleIdList);
        if(CollectionUtils.isEmpty(userAclIdList)){
            return Lists.newArrayList();
        }
        return sysAclMapper.getByIdList(userAclIdList);
    }
    public boolean isSuperAdmin(){
        return true;
    }
}

package com.ljtao3.service;


import com.google.common.collect.Lists;
import com.ljtao3.dao.SysRoleUserMapper;
import com.ljtao3.dao.SysUserMapper;
import com.ljtao3.model.SysRoleUser;
import com.ljtao3.model.SysUser;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SysRoleUserService {
    @Resource
    private SysRoleUserMapper sysRoleUserMapper;
    @Resource
    private SysUserMapper sysUserMapper;

    public List<SysUser> getListByRoleId(Integer roleId){
        List<Integer> userIdList = sysRoleUserMapper.getUserIdListByRoleId(roleId);
        if(CollectionUtils.isEmpty(userIdList)){
            Lists.newArrayList();
        }
        List<SysUser> userList=sysUserMapper.getByIdList(userIdList);
        return userList;
    }
}

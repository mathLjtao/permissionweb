package com.ljtao3.service;


import com.google.common.collect.Lists;
import com.ljtao3.common.MyRequestHolder;
import com.ljtao3.dao.SysRoleUserMapper;
import com.ljtao3.dao.SysUserMapper;
import com.ljtao3.model.SysRole;
import com.ljtao3.model.SysRoleUser;
import com.ljtao3.model.SysUser;
import com.ljtao3.util.IpUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysRoleUserService {
    @Resource
    private SysRoleUserMapper sysRoleUserMapper;
    @Resource
    private SysUserMapper sysUserMapper;

    public List<SysUser> getListByRoleId(Integer roleId){
        List<Integer> userIdList = sysRoleUserMapper.getUserIdListByRoleId(roleId);
        if(CollectionUtils.isEmpty(userIdList)){
            return Lists.newArrayList();
        }
        return sysUserMapper.getByIdList(userIdList);
    }
    public void changeUsers(Integer roleId, Set<Integer> userIds) {
        //查出原始数据
        List<Integer> originUserIds = sysRoleUserMapper.getUserIdListByRoleId(roleId);
        //对比是不是一样的数据
        if(originUserIds.size()==userIds.size()){
            originUserIds.removeAll(userIds);
            if(CollectionUtils.isEmpty(originUserIds)){
                return;
            }
        }
        updateRoleUsers(roleId,userIds);
    }
    @Transactional(rollbackFor = Exception.class)
    public void updateRoleUsers(Integer roleId, Set<Integer> userIds){
        //删掉原始数据
        sysRoleUserMapper.deleteByRoleId(roleId);

        //加入所选数据
        if(userIds==null && userIds.size()<1){
            return ;
        }
        List<SysRoleUser> roleUserList=Lists.newArrayList();
        for(Integer  userId:userIds){
            SysRoleUser sysRoleUser=SysRoleUser.builder().roleId(roleId).userId(userId).operateTime(new Date())
                    .operator(MyRequestHolder.getCurrentUser().getUsername())
                    .operateIp(IpUtil.getRemoteIp(MyRequestHolder.getCurrentRequest())).build();
            roleUserList.add(sysRoleUser);
        }
        sysRoleUserMapper.batchInsert(roleUserList);
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

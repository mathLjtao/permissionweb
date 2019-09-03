package com.ljtao3.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ljtao3.common.MyRequestHolder;
import com.ljtao3.dao.SysRoleAclMapper;
import com.ljtao3.model.SysRoleAcl;
import com.ljtao3.util.IpUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
@Service
public class SysRoleAclService {

    @Resource
    private SysRoleAclMapper sysRoleAclMapper;
    @Resource
    private SysLogService sysLogService;
    /*

     */
    private boolean checkExist(Integer roleId, Integer aclId ){
        return sysRoleAclMapper.countByRoleIdAndAclIds(roleId,aclId)>0;
    }
    @Transactional
    public void changeAcls(Integer roleId, List<Integer> aclIds) {

        //获取数据中原始的role-->acl的数据， 并判断是不是没有改变原始数据
        List<Integer> originAclIds = sysRoleAclMapper.getAclIdListByRoleId(roleId);
        HashSet<Integer> originAclIdSet = Sets.newHashSet(originAclIds);
        HashSet<Integer> aclIdSet = Sets.newHashSet(aclIds);
        if(originAclIds.size()==aclIds.size()){
            originAclIdSet.removeAll(aclIdSet);
            if(CollectionUtils.isEmpty(originAclIdSet)){
                return ;
            }
        }
        //删除数据库中原来的数据
        sysRoleAclMapper.deleteByRoleId(roleId);
        if(aclIds==null || aclIds.isEmpty()){
            sysLogService.saveRoleAclLog(roleId,originAclIds,aclIds);
            return ;
        }
        //要加入到数据库的数据
        List<SysRoleAcl> roleAclList= Lists.newArrayList();
        for(Integer aclId:aclIds){
            SysRoleAcl sysRoleAcl=SysRoleAcl.builder().operator(MyRequestHolder.getCurrentUser().getUsername())
                    .operateIp(IpUtil.getRemoteIp(MyRequestHolder.getCurrentRequest()))
                    .operateTime(new Date()).roleId(roleId).aclId(aclId).build();
            roleAclList.add(sysRoleAcl);

        }
        sysRoleAclMapper.batchInsert(roleAclList);
        sysLogService.saveRoleAclLog(roleId,originAclIds,aclIds);
    }
}

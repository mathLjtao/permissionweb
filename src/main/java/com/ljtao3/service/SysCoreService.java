package com.ljtao3.service;

import com.google.common.collect.Lists;
import com.ljtao3.beans.CacheKeyConstants;
import com.ljtao3.common.MyRequestHolder;
import com.ljtao3.dao.SysAclMapper;
import com.ljtao3.dao.SysRoleAclMapper;
import com.ljtao3.dao.SysRoleUserMapper;
import com.ljtao3.model.SysAcl;
import com.ljtao3.util.JsonMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysCoreService {
    @Resource
    private SysAclMapper sysAclMapper;
    @Resource
    private SysRoleUserMapper sysRoleUserMapper;
    @Resource
    private SysRoleAclMapper sysRoleAclMapper;
    @Resource
    private SysCacheService sysCacheService;

    public List<SysAcl> getCurrentUserAclList(){
        int userId= MyRequestHolder.getCurrentUser().getId();
        //int userId=9;
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
        //todo：可以指定某个用户、角色
        String userName=MyRequestHolder.getCurrentUser().getUsername();
        if("admin".equals(userName) || "ljtao3".equals(userName)){
            return true;
        }
        return false;
    }

    public boolean hasUrlAcl(String servletPath) {
        if(isSuperAdmin()){
            return true;
        }
        //获取 存在该url的权限数据
        List<SysAcl> aclList=sysAclMapper.getByUrl(servletPath);
        if(CollectionUtils.isEmpty(aclList)){
            //如果在数据库没有配置权限控制，那么就直接通过
//            return false;
            return true;
        }

        List<SysAcl> userAclList=getCurrentUserAclListFromCache();
        Set<Integer> userAclIdSet=userAclList.stream().map(acl->acl.getId()).collect(Collectors.toSet());

        //规则:只要有一个权限点有权限，那么我们就认为有访问权限
        //  /sys/user/action.json
        //判断一个用户书否具有某个权限点的访问权限
        for (SysAcl acl:aclList){
            if(acl==null || acl.getStatus()!=1){
                //权限点无效
                continue;
            }
            if(userAclIdSet.contains(acl.getId())){
                return true;
            }
        }
        return false;
    }
    //从缓存中获取用户的权限数据
    public List<SysAcl> getCurrentUserAclListFromCache(){
        int userId=MyRequestHolder.getCurrentUser().getId();
        String valueCache=sysCacheService.getFormCache(CacheKeyConstants.USER_ACLS,String.valueOf(userId));
        if (StringUtils.isBlank(valueCache)){
            List<SysAcl> aclList=getCurrentUserAclList();
            if(CollectionUtils.isNotEmpty(aclList)){
                sysCacheService.saveCache(JsonMapper.obj2String(aclList),300,CacheKeyConstants.USER_ACLS,String.valueOf(userId));
            }
            return aclList;
        }
        return JsonMapper.String2obj(valueCache, new TypeReference<List<SysAcl>>() { });
    }
}

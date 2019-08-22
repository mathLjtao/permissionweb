package com.ljtao3.service;

import com.google.common.base.Preconditions;
import com.ljtao3.common.MyRequestHolder;
import com.ljtao3.dao.SysAclModuleMapper;
import com.ljtao3.exception.ParamException;
import com.ljtao3.model.SysAclModule;
import com.ljtao3.model.SysDept;
import com.ljtao3.param.AclModuleParam;
import com.ljtao3.util.IpUtil;
import com.ljtao3.util.LevelUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SysAclModuleService {
    @Resource
    private SysAclModuleMapper sysAclModuleMapper;

    public void save(AclModuleParam param){
        if(checkExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("同一层级下存在相同名称的权限模块！");
        }
        SysAclModule aclModule=SysAclModule.builder().name(param.getName()).parentId(param.getParentId())
                .level(param.getLevel()).seq(param.getSeq()).status(param.getStatus()).remark(param.getRemark()).build();
        aclModule.setLevel(LevelUtils.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
        aclModule.setOperator(MyRequestHolder.getCurrentUser().getUsername());
        aclModule.setOperateIp(IpUtil.getRemoteIp(MyRequestHolder.getCurrentRequest()));
        aclModule.setOperateTime(new Date());
        sysAclModuleMapper.insertSelective(aclModule);
    }
    public void update(AclModuleParam param){
        int id=param.getId();
        if(checkExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("同一层级下存在相同名称的权限模块！");
        }
        SysAclModule before=sysAclModuleMapper.selectByPrimaryKey(id);
        Preconditions.checkNotNull(before,"待更新的权限模块不存在！");
        SysAclModule after=SysAclModule.builder().id(id).name(param.getName()).parentId(param.getParentId())
                .seq(param.getSeq()).status(param.getStatus()).remark(param.getRemark()).build();
        after.setLevel(LevelUtils.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
        after.setOperator(MyRequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(MyRequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        updateWithChild(before,after);
    }
    //更新该权限模块的level
    @Transactional
    public void updateWithChild(SysAclModule before,SysAclModule after){
        String newLevelPrefix=after.getLevel();
        String oldLevelPrefix=before.getLevel();
        if(!newLevelPrefix.equals(oldLevelPrefix)){
            List<SysAclModule> childListByLevel = sysAclModuleMapper.getChildAclModuleListByLevel(LevelUtils.calculateLevel(oldLevelPrefix,before.getId()));
            if(CollectionUtils.isNotEmpty(childListByLevel)){
                for(SysAclModule aclModule:childListByLevel){
                    aclModule.setLevel(newLevelPrefix+aclModule.getLevel().substring(oldLevelPrefix.length()));
                }
                sysAclModuleMapper.batchUpdateLevel(childListByLevel);
            }
        }
        sysAclModuleMapper.updateByPrimaryKeySelective(after);

    }
    private boolean checkExist(Integer parentId,String aclModuleName,Integer aclModuleId){
        return sysAclModuleMapper.countByIdAndNameAndParentId(aclModuleId,aclModuleName,parentId)>0;
    }
    private String getLevel(Integer aclModuleId){
        SysAclModule sysAclModule = sysAclModuleMapper.selectByPrimaryKey(aclModuleId);
        if(sysAclModule==null)
            return null;
        return  sysAclModule.getLevel();
    }
}

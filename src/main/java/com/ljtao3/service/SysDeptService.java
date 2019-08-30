package com.ljtao3.service;

import com.google.common.base.Preconditions;
import com.ljtao3.common.MyRequestHolder;
import com.ljtao3.dao.SysDeptMapper;
import com.ljtao3.dao.SysUserMapper;
import com.ljtao3.exception.ParamException;
import com.ljtao3.model.SysDept;
import com.ljtao3.param.DeptParam;
import com.ljtao3.util.IpUtil;
import com.ljtao3.util.LevelUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SysDeptService {
    @Resource
    private SysDeptMapper sysDeptMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SysLogService sysLogService;

    public void save(DeptParam param){
        if(checkExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("同一层级下存在相同名称的部门！");
        }
        SysDept dept=SysDept.builder().name(param.getName()).parentId(param.getParentId())
                .seq(param.getSeq()).remark(param.getRemark()).build();
        dept.setLevel(LevelUtils.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
        dept.setOperator(MyRequestHolder.getCurrentUser().getUsername());
        dept.setOperateIp(IpUtil.getRemoteIp(MyRequestHolder.getCurrentRequest()));
        dept.setOperateTime(new Date());
        sysDeptMapper.insertSelective(dept);
        sysLogService.saveDeptLog(null,dept);
    }

    public void update(DeptParam param) {
        SysDept before=sysDeptMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的部门不存在");
        if(checkExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("同一层级下存在相同名称的部门！");
        }
        SysDept after=SysDept.builder().id(param.getId()).name(param.getName()).parentId(param.getParentId())
                .seq(param.getSeq()).remark(param.getRemark()).build();
        after.setLevel(LevelUtils.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
        after.setOperator(MyRequestHolder.getCurrentUser().getUsername());
        after.setOperateTime(new Date());
        after.setOperateIp(IpUtil.getRemoteIp(MyRequestHolder.getCurrentRequest()));
        updateWithChild(before,after);
        sysLogService.saveDeptLog(before,after);
    }
    @Transactional
    public void updateWithChild(SysDept before,SysDept after ){
        //List<SysDept> childDeptListByLevel = sysDeptMapper.getChildDeptListByLevel("0");
        String newLevelPrefix=after.getLevel();
        String oldLevelPrefix=before.getLevel();
        if(!newLevelPrefix.equals(oldLevelPrefix)){
            List<SysDept> childDeptListByLevel = sysDeptMapper.getChildDeptListByLevel(LevelUtils.calculateLevel(oldLevelPrefix,before.getId()));
            if(CollectionUtils.isNotEmpty(childDeptListByLevel)){
                for(SysDept dept:childDeptListByLevel){
                    dept.setLevel(newLevelPrefix+dept.getLevel().substring(oldLevelPrefix.length()));
                }
                sysDeptMapper.batchUpdateLevel(childDeptListByLevel);
            }
        }
        sysDeptMapper.updateByPrimaryKeySelective(after);
    }
    private boolean checkExist(Integer parentId,String deptName,Integer deptId){
        return sysDeptMapper.countByIdAndNameAndParentId(deptId,deptName,parentId)>0;
    }
    private String getLevel(Integer deptId){
        SysDept dept=sysDeptMapper.selectByPrimaryKey(deptId);
        if(dept==null){
            return  null;
        }
        return dept.getLevel();
    }

    public void deleteById(Integer deptId) {
        SysDept sysDept=sysDeptMapper.selectByPrimaryKey(deptId);
        Preconditions.checkNotNull(sysDept,"待删除的部门不存在！");
        if(sysDeptMapper.countByParentId(sysDept.getId())>0){
            throw new ParamException("该部门存在子部门，无法删除！");
        }
        if(sysUserMapper.countByDeptId(deptId)>0){
            throw new ParamException("该部门下存在用户，无法删除！");
        }
        System.out.println("执行删除:"+deptId);
        //sysDeptMapper.deleteByPrimaryKey(deptId);
    }
}

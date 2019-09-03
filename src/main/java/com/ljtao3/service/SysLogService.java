package com.ljtao3.service;

import com.google.common.base.Preconditions;
import com.ljtao3.beans.LogType;
import com.ljtao3.beans.PageQuery;
import com.ljtao3.beans.PageResult;
import com.ljtao3.common.MyRequestHolder;
import com.ljtao3.dao.*;
import com.ljtao3.exception.ParamException;
import com.ljtao3.model.*;
import com.ljtao3.param.*;
import com.ljtao3.util.IpUtil;
import com.ljtao3.util.JsonMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class SysLogService {
    @Resource
    private SysLogMapper sysLogMapper;
    @Resource
    private SysDeptMapper sysDeptMapper;
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private SysRoleUserMapper sysRoleUserMapper;
    @Resource
    private SysRoleUserService sysRoleUserService;
    @Resource
    private SysRoleAclService sysRoleAclService;
    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private SysAclService sysAclService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private SysDeptService sysDeptService;
    @Resource
    private SysAclModuleService sysAclModuleService;
    @Resource
    private  SysAclModuleMapper sysAclModuleMapper;

    private void recoverDept(SysLogWithBLOBs log){
        //直接调用 SysDeptService 的方法，这样才能把子部门也还原回去
        if(StringUtils.isBlank(log.getOldValue()) || StringUtils.isBlank(log.getNewValue())){
            throw new ParamException("新增和删除不做还原！");
        }
        DeptParam deptParam = JsonMapper.String2obj(log.getOldValue(), new TypeReference<DeptParam>() {
        });
        sysDeptService.update(deptParam);
    }
    private void recoverUser(SysLogWithBLOBs log) {
        if(StringUtils.isBlank(log.getOldValue()) || StringUtils.isBlank(log.getNewValue())){
            throw new ParamException("新增和删除不做还原！");
        }
        UserParam userParam = JsonMapper.String2obj(log.getOldValue(), new TypeReference<UserParam>() {
        });
        sysUserService.update(userParam);
    }
    private void recoverAclModule(SysLogWithBLOBs log) {
        /**/
        if(StringUtils.isBlank(log.getOldValue()) || StringUtils.isBlank(log.getNewValue())){
            throw new ParamException("新增和删除不做还原！");
        }
        AclModuleParam param = JsonMapper.String2obj(log.getOldValue(), new TypeReference<AclModuleParam>() {
        });
        sysAclModuleService.update(param);


    }
    private void recoverAcl(SysLogWithBLOBs log) {
        if(StringUtils.isBlank(log.getOldValue()) || StringUtils.isBlank(log.getNewValue())){
            throw new ParamException("新增和删除不做还原！");
        }
        //这里直接变成AclParam类型数据就行了，跟下面recoverRole的对比进化了
        AclParam aclParam = JsonMapper.String2obj(log.getOldValue(), new TypeReference<AclParam>() {
        });
        sysAclService.update(aclParam);
    }
    private void recoverRole(SysLogWithBLOBs log) {
        if(StringUtils.isBlank(log.getOldValue()) || StringUtils.isBlank(log.getNewValue())){
            throw new ParamException("新增和删除不做还原！");
        }
        SysRole afterRole = JsonMapper.String2obj(log.getOldValue(), new TypeReference<SysRole>() {
        });
        //直接调用SysRoleService的方法来更新
        RoleParam param=new RoleParam();
        param.setId(afterRole.getId());
        param.setName(afterRole.getName());
        param.setRemark(afterRole.getRemark());
        param.setType(afterRole.getType());
        param.setStatus(afterRole.getStatus());
        sysRoleService.update(param);
    }
    private void recoverRoleAcl(SysLogWithBLOBs log) {
        SysRole role=sysRoleMapper.selectByPrimaryKey(log.getTargetId());
        Preconditions.checkNotNull(role,"roleAclRecover:角色已经不存在");
        sysRoleAclService.changeAcls(role.getId(),JsonMapper.String2obj(log.getOldValue(), new TypeReference<List<Integer>>() {
        }));
    }
    private void recoverRoleUser(SysLogWithBLOBs log) {
        SysRole role = sysRoleMapper.selectByPrimaryKey(log.getTargetId());
        Preconditions.checkNotNull(role,"roleUserRecover:角色已经不存在！");
        sysRoleUserService.changeUsers(log.getTargetId(),JsonMapper.String2obj(log.getOldValue(), new TypeReference<Set<Integer>>() {}));
    }

    public void recover(Integer id) {
        SysLogWithBLOBs sysLog = sysLogMapper.selectByPrimaryKey(id);
        Preconditions.checkNotNull(sysLog,"待还原的数据不存在");
        switch(sysLog.getType()){
            case LogType.TYPE_DEPT:
                recoverDept(sysLog);
                break;
            case LogType.TYPE_USER:
                recoverUser(sysLog);
                break;
            case LogType.TYPE_ACL_MODULE:
                recoverAclModule(sysLog);
                break;
            case LogType.TYPE_ACL:
                recoverAcl(sysLog);
                break;
            case LogType.TYPE_ROLE:
                recoverRole(sysLog);
                break;
            case LogType.TYPE_ROLE_ACL:
                recoverRoleAcl(sysLog);
                break;
            case LogType.TYPE_ROLE_USER:
                recoverRoleUser(sysLog);
                break;
            default:;
        }
    }



    public void saveDeptLog(SysDept before,SysDept after){
        SysLogWithBLOBs sysLog=new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_DEPT);
        sysLog.setTargetId(after==null?before.getId():after.getId());
        sysLog.setOldValue(before==null?"": JsonMapper.obj2String(before));
        sysLog.setNewValue(after==null?"":JsonMapper.obj2String(after));
        sysLog.setOperator(MyRequestHolder.getCurrentUser().getUsername());
        sysLog.setOperateIp(IpUtil.getRemoteIp(MyRequestHolder.getCurrentRequest()));
        sysLog.setOperateTime(new Date());
        sysLogMapper.insertSelective(sysLog);
    }
    public void saveUserLog(SysUser before, SysUser after){
        SysLogWithBLOBs sysLog=new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_USER);
        sysLog.setTargetId(after==null?before.getId():after.getId());
        sysLog.setOldValue(before==null?"": JsonMapper.obj2String(before));
        sysLog.setNewValue(after==null?"":JsonMapper.obj2String(after));
        sysLog.setOperator(MyRequestHolder.getCurrentUser().getUsername());
        sysLog.setOperateIp(IpUtil.getRemoteIp(MyRequestHolder.getCurrentRequest()));
        sysLog.setOperateTime(new Date());
        sysLogMapper.insertSelective(sysLog);
    }
    public void saveAclModuleLog(SysAclModule before, SysAclModule after){
        SysLogWithBLOBs sysLog=new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_ACL_MODULE);
        sysLog.setTargetId(after==null?before.getId():after.getId());
        sysLog.setOldValue(before==null?"": JsonMapper.obj2String(before));
        sysLog.setNewValue(after==null?"":JsonMapper.obj2String(after));
        sysLog.setOperator(MyRequestHolder.getCurrentUser().getUsername());
        sysLog.setOperateIp(IpUtil.getRemoteIp(MyRequestHolder.getCurrentRequest()));
        sysLog.setOperateTime(new Date());
        sysLogMapper.insertSelective(sysLog);
    }
    public void saveAclLog(SysAcl before, SysAcl after){
        SysLogWithBLOBs sysLog=new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_ACL);
        sysLog.setTargetId(after==null?before.getId():after.getId());
        sysLog.setOldValue(before==null?"": JsonMapper.obj2String(before));
        sysLog.setNewValue(after==null?"":JsonMapper.obj2String(after));
        sysLog.setOperator(MyRequestHolder.getCurrentUser().getUsername());
        sysLog.setOperateIp(IpUtil.getRemoteIp(MyRequestHolder.getCurrentRequest()));
        sysLog.setOperateTime(new Date());
        sysLogMapper.insertSelective(sysLog);
    }
    public void saveRoleLog(SysRole before, SysRole after){
        SysLogWithBLOBs sysLog=new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_ROLE);
        sysLog.setTargetId(after==null?before.getId():after.getId());
        sysLog.setOldValue(before==null?"": JsonMapper.obj2String(before));
        sysLog.setNewValue(after==null?"":JsonMapper.obj2String(after));
        sysLog.setOperator(MyRequestHolder.getCurrentUser().getUsername());
        sysLog.setOperateIp(IpUtil.getRemoteIp(MyRequestHolder.getCurrentRequest()));
        sysLog.setOperateTime(new Date());
        sysLogMapper.insertSelective(sysLog);
    }
    public void saveRoleAclLog(int roleId, List<Integer> before, List<Integer> after){
        SysLogWithBLOBs sysLog=new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_ROLE_ACL);
        sysLog.setTargetId(roleId);
        sysLog.setOldValue(before==null?"": JsonMapper.obj2String(before));
        sysLog.setNewValue(after==null?"":JsonMapper.obj2String(after));
        sysLog.setOperator(MyRequestHolder.getCurrentUser().getUsername());
        sysLog.setOperateIp(IpUtil.getRemoteIp(MyRequestHolder.getCurrentRequest()));
        sysLog.setOperateTime(new Date());
        sysLogMapper.insertSelective(sysLog);
    }
    public void saveRoleUserLog(int roleId,List<Integer> before, Set<Integer> after){
        SysLogWithBLOBs sysLog=new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_ROLE_USER);
        sysLog.setTargetId(roleId);
        sysLog.setOldValue(before==null?"": JsonMapper.obj2String(before));
        sysLog.setNewValue(after==null?"":JsonMapper.obj2String(after));
        sysLog.setOperator(MyRequestHolder.getCurrentUser().getUsername());
        sysLog.setOperateIp(IpUtil.getRemoteIp(MyRequestHolder.getCurrentRequest()));
        sysLog.setOperateTime(new Date());
        sysLogMapper.insertSelective(sysLog);
    }

    public List<SysLogWithBLOBs> getAll() {
        return sysLogMapper.getAll();
    }

    public PageResult<SysLogWithBLOBs> getByCondWithPage(SearchLogParam param , PageQuery pageQuery) {
        pageQuery.setOffset();
        int count=sysLogMapper.getCountByCond(param);

        try{
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if(StringUtils.isNotEmpty(param.getFromTime())){
               simpleDateFormat.parse(param.getFromTime());
            }
            if(StringUtils.isNotEmpty(param.getToTime())){
               simpleDateFormat.parse(param.getToTime());
            }
        }catch (Exception e){
            throw  new ParamException("传入的日期格式有问题，正确格式为：yyyy-MM-dd HH:mm:ss");
        }
        if(count>0){
            List<SysLogWithBLOBs> list=sysLogMapper.getByCondWithPage( param ,  pageQuery);
            if(CollectionUtils.isNotEmpty(list)){
                return new PageResult(count,list);
            }
        }
        return new PageResult();
    }





}

package com.ljtao3.service;

import com.ljtao3.beans.LogType;
import com.ljtao3.beans.PageQuery;
import com.ljtao3.beans.PageResult;
import com.ljtao3.common.MyRequestHolder;
import com.ljtao3.dao.SysLogMapper;
import com.ljtao3.exception.ParamException;
import com.ljtao3.model.*;
import com.ljtao3.param.LogParam;
import com.ljtao3.util.IpUtil;
import com.ljtao3.util.JsonMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class SysLogService {
    @Resource
    private SysLogMapper sysLogMapper;

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

    public PageResult<SysLogWithBLOBs> getByCondWithPage(LogParam param , PageQuery pageQuery) {
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

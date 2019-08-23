package com.ljtao3.service;

import com.google.common.base.Preconditions;
import com.ljtao3.common.MyRequestHolder;
import com.ljtao3.dao.SysRoleMapper;
import com.ljtao3.exception.ParamException;
import com.ljtao3.model.SysRole;
import com.ljtao3.param.RoleParam;
import com.ljtao3.util.IpUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SysRoleService {
    @Resource
    private SysRoleMapper sysRoleMapper;

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
}

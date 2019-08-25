package com.ljtao3.service;


import com.ljtao3.dao.SysRoleUserMapper;
import com.ljtao3.model.SysRoleUser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SysRoleUserService {
    @Resource
    private SysRoleUserMapper sysRoleUserMapper;
    public List<SysRoleUser> getListByRoleId(Integer roleId){
        List<Integer> userIdListByRoleId = sysRoleUserMapper.getUserIdListByRoleId(roleId);
        return null;
    }
}

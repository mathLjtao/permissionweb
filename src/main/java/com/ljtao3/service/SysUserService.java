package com.ljtao3.service;

import com.ljtao3.dao.SysUserMapper;
import com.ljtao3.model.SysUser;
import com.ljtao3.param.UserParam;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SysUserService {
    @Resource
    private SysUserMapper sysUserMapper;
    public void save(UserParam param){
        String password="123456";
        SysUser sysUser=SysUser.builder().username(param.getUsername()).telephone(param.getTelephone()).mail(param.getMail()).deptId(param.getDeptId())
                .password(password).status(param.getStatus()).remark(param.getRemark()).build();

    }
    public boolean checkEmailExist(String mail,Integer userId){
        return false;
    }
    public boolean checkTelephoneExist(String phone,Integer userId){
        return false;
    }

}

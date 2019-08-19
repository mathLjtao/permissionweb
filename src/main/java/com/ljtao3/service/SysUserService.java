package com.ljtao3.service;

import com.ljtao3.dao.SysUserMapper;
import com.ljtao3.model.SysUser;
import com.ljtao3.param.UserParam;
import com.ljtao3.util.PasswordUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class SysUserService {
    @Resource
    private SysUserMapper sysUserMapper;
    public void save(UserParam param){
        Integer id = param.getId();
        checkEmailExist(param.getMail(),id);
        checkTelephoneExist(param.getTelephone(),id);
        String password= PasswordUtil.randomPassword();
        password="123456";//todo
        SysUser user=SysUser.builder().username(param.getUsername()).telephone(param.getTelephone()).mail(param.getMail()).deptId(param.getDeptId())
                .password(password).status(param.getStatus()).remark(param.getRemark()).build();
        user.setOperator("system");
        user.setOperateIp("127.0.0.1");
        user.setOperateTime(new Date());
        //TODO : sendEmail
        sysUserMapper.insertSelective(user);
    }
    public boolean checkEmailExist(String mail,Integer userId){
        return false;
    }
    public boolean checkTelephoneExist(String phone,Integer userId){
        return false;
    }

}

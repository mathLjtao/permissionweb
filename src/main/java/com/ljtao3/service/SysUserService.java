package com.ljtao3.service;

import com.google.common.base.Preconditions;
import com.ljtao3.beans.PageQuery;
import com.ljtao3.beans.PageResult;
import com.ljtao3.dao.SysUserMapper;
import com.ljtao3.exception.ParamException;
import com.ljtao3.model.SysUser;
import com.ljtao3.param.UserParam;
import com.ljtao3.util.MD5Util;
import com.ljtao3.util.PasswordUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SysUserService {
    @Resource
    private SysUserMapper sysUserMapper;
    public void save(UserParam param){
        Integer id = param.getId();
        if(checkEmailExist(param.getMail(),id)){
            throw new ParamException("邮箱被占用");
        }
        if(checkTelephoneExist(param.getTelephone(),id)){
            throw new ParamException("手机号码被占用");
        }

        String password= PasswordUtil.randomPassword();
        password="123456";//todo
        String pwMD5 = MD5Util.encrypt(password);
        SysUser user=SysUser.builder().username(param.getUsername()).telephone(param.getTelephone()).mail(param.getMail()).deptId(param.getDeptId())
                .password(pwMD5).status(param.getStatus()).remark(param.getRemark()).build();
        user.setOperator("system insert");
        user.setOperateIp("127.0.0.1");
        user.setOperateTime(new Date());
        //TODO : sendEmail
        sysUserMapper.insertSelective(user);
    }
    public void update(UserParam param){
        Integer id = param.getId();
        if(checkEmailExist(param.getMail(),id)){
            throw new ParamException("邮箱被占用！");
        }
        if(checkTelephoneExist(param.getTelephone(),id)){
            throw new ParamException("手机号码被占用！");
        }
        SysUser before=sysUserMapper.selectByPrimaryKey(id);
        Preconditions.checkNotNull(before,"待更新的用户不存在！");
        SysUser after=SysUser.builder().username(param.getUsername()).telephone(param.getTelephone()).mail(param.getMail()).deptId(param.getDeptId())
                .status(param.getStatus()).remark(param.getRemark()).id(id).build();
        after.setOperator("system update");
        after.setOperateIp("127.0.0.1");
        after.setOperateTime(new Date());
        //这个更新是有值才会覆盖掉原来的值
        sysUserMapper.updateByPrimaryKeySelective(after);
    }
    public boolean checkEmailExist(String mail,Integer userId){
        /*
        一开始自己是这样写的，这算是一块小小的代码优化
         if(sysUserMapper.checkEmailExist(mail)>0)
            return true;
        return false;
         */
        return sysUserMapper.countByMail(mail)>0;
    }
    public boolean checkTelephoneExist(String phone,Integer userId){
        return sysUserMapper.countByTelephone(phone)>0;
    }

    public SysUser findByKeyword(String username) {
        return sysUserMapper.findByKeyword(username);
    }
    public PageResult<SysUser> getPageByDeptId(Integer deptId, PageQuery pageQuery){
        int total=sysUserMapper.countByDeptId(deptId);
        List<SysUser> list=new ArrayList<>();


        if (total>0){
            list= sysUserMapper.getPageByDeptId(deptId, pageQuery);
            return  new PageResult<SysUser>(total,list);
        }
        //return PageResult.builder().build();
        return new PageResult<SysUser>();
    }
}

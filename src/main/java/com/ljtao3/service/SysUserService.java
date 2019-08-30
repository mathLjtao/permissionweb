package com.ljtao3.service;

import com.google.common.base.Preconditions;
import com.ljtao3.beans.PageQuery;
import com.ljtao3.beans.PageResult;
import com.ljtao3.common.MyRequestHolder;
import com.ljtao3.dao.SysUserMapper;
import com.ljtao3.exception.ParamException;
import com.ljtao3.model.SysUser;
import com.ljtao3.param.UserParam;
import com.ljtao3.util.IpUtil;
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
    @Resource
    private SysLogService sysLogService;
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
        user.setOperator(MyRequestHolder.getCurrentUser().getUsername());
        user.setOperateIp(IpUtil.getRemoteIp(MyRequestHolder.getCurrentRequest()));
        user.setOperateTime(new Date());
        //TODO : sendEmail
        sysUserMapper.insertSelective(user);
        sysLogService.saveUserLog(null,user);
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
        after.setOperator(MyRequestHolder.getCurrentUser().getUsername());
        after.setOperateIp("外网IP："+IpUtil.getRemoteIp(MyRequestHolder.getCurrentRequest())
                +"，用户真实IP："+IpUtil.getUserIP(MyRequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        //这个更新是有值才会覆盖掉原来的值
        sysUserMapper.updateByPrimaryKeySelective(after);
        sysLogService.saveUserLog(before,after);
    }
    public boolean checkEmailExist(String mail,Integer userId){
        /*
        一开始自己是这样写的，这算是一块小小的代码优化
         if(sysUserMapper.checkEmailExist(mail)>0)
            return true;
        return false;
         */
        return sysUserMapper.countByMail(mail,userId)>0;
    }
    public boolean checkTelephoneExist(String phone,Integer userId){
        return sysUserMapper.countByTelephone(phone,userId)>0;
    }

    public SysUser findByKeyword(String username) {
        return sysUserMapper.findByKeyword(username);
    }
    public PageResult<SysUser> getPageByDeptId(Integer deptId, PageQuery pageQuery){
        pageQuery.setOffset();
        int total=sysUserMapper.countByDeptId(deptId);
        List<SysUser> list=new ArrayList<>();
        if (total>0){
            list= sysUserMapper.getPageByDeptId(deptId, pageQuery);
            return  new PageResult<SysUser>(total,list);
        }
        //return PageResult.builder().build();
        return new PageResult<SysUser>();
    }

    public List<SysUser> getAll(){
        return sysUserMapper.getAll();
    }

}

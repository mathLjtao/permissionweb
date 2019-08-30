package com.ljtao3.service;

import com.google.common.base.Preconditions;
import com.ljtao3.beans.PageQuery;
import com.ljtao3.beans.PageResult;
import com.ljtao3.common.MyRequestHolder;
import com.ljtao3.dao.SysAclMapper;
import com.ljtao3.exception.ParamException;
import com.ljtao3.model.SysAcl;
import com.ljtao3.param.AclParam;
import com.ljtao3.util.IpUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class SysAclService {
    @Resource
    private SysAclMapper sysAclMapper;
    @Resource
    private SysLogService sysLogService;
    public PageResult<SysAcl>  getPageByAclModuleId(Integer aclModuleId, PageQuery pageQuery){
        PageResult<SysAcl> page=new PageResult<SysAcl>();
        pageQuery.setOffset();
        List<SysAcl> sysList=sysAclMapper.getPageByAclModuleId(aclModuleId,pageQuery);
        int total=sysAclMapper.countByAclModuleId(aclModuleId);
        page.setTotal(total);
        page.setData(sysList);
        return page;
    }

    public void save(AclParam param) {
        if (checkExist(param.getId(),param.getName(),param.getAclModuleId())){
            throw new ParamException("同一层级下存在相同名称的权限点!");
        }
        SysAcl sysAcl=SysAcl.builder().name(param.getName()).aclModuleId(param.getAclModuleId()).url(param.getUrl()).type(param.getType()).seq(param.getSeq())
                .status(param.getStatus()).remark(param.getRemark()).build();
        sysAcl.setCode(generateCode());
        sysAcl.setOperator(MyRequestHolder.getCurrentUser().getUsername());
        sysAcl.setOperateTime(new Date());
        sysAcl.setOperateIp(IpUtil.getRemoteIp(MyRequestHolder.getCurrentRequest()));
        sysAclMapper.insertSelective(sysAcl);
        sysLogService.saveAclLog(null,sysAcl);
    }

    public void update(AclParam param) {
        if (checkExist(param.getId(),param.getName(),param.getAclModuleId())){
            throw new ParamException("同一层级下存在相同名称的权限点!");
        }
        SysAcl before=sysAclMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的权限点不存在");
        SysAcl sysAcl=SysAcl.builder().id(param.getId()).name(param.getName()).aclModuleId(param.getAclModuleId()).url(param.getUrl()).type(param.getType())
                .seq(param.getSeq()).status(param.getStatus()).remark(param.getRemark()).build();
        sysAcl.setOperator(MyRequestHolder.getCurrentUser().getUsername());
        sysAcl.setOperateTime(new Date());
        sysAcl.setOperateIp(IpUtil.getRemoteIp(MyRequestHolder.getCurrentRequest()));
        sysAclMapper.updateByPrimaryKeySelective(sysAcl);
        sysLogService.saveAclLog(before,sysAcl);
    }
    public boolean checkExist(Integer id,String name,Integer aclModuleId){
        int i=sysAclMapper.countByNameAndAclModuleId(id,name,aclModuleId);
        return i>0;
    }
    public String generateCode(){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMddHHmmdd");
        String code = simpleDateFormat.format(new Date())+"_"+(int)(Math.random()*100);
        return code;
    }
}

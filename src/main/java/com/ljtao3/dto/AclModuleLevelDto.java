package com.ljtao3.dto;

import com.ljtao3.model.SysAclModule;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@ToString
public class AclModuleLevelDto extends SysAclModule {
    public List<AclModuleLevelDto> getAclModuleList() {
        return aclModuleList;
    }

    public  void setAclModuleList(List<AclModuleLevelDto> aclModuleList) {
        this.aclModuleList = aclModuleList;
    }

    private List<AclModuleLevelDto> aclModuleList=new ArrayList<>();
    public static AclModuleLevelDto adapt(SysAclModule aclModule){
        AclModuleLevelDto dto=new AclModuleLevelDto();
        BeanUtils.copyProperties(aclModule,dto);
        return dto;
    }
}

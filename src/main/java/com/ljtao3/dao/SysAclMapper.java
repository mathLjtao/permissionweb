package com.ljtao3.dao;

import com.ljtao3.beans.PageQuery;
import com.ljtao3.model.SysAcl;
import org.apache.ibatis.annotations.Param;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;

import java.util.List;

public interface SysAclMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysAcl record);

    int insertSelective(SysAcl record);

    SysAcl selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysAcl record);

    int updateByPrimaryKey(SysAcl record);

    int countByAclModuleId(@Param("aclModuleId")Integer aclModuleId);

    List<SysAcl> getPageByAclModuleId(@Param("aclModuleId") Integer aclModuleId,@Param("page") PageQuery page);

    int countByNameAndAclModuleId(@Param("id") Integer id,@Param("name") String name,@Param("aclModuleId") Integer aclModuleId);
}
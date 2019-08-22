package com.ljtao3.dao;

import com.ljtao3.model.SysAclModule;
import com.ljtao3.model.SysDept;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysAclModuleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysAclModule record);

    int insertSelective(SysAclModule record);

    SysAclModule selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysAclModule record);

    int updateByPrimaryKey(SysAclModule record);

    List<SysAclModule> getChildAclModuleListByLevel(String level);

    void batchUpdateLevel(@Param("sysAclModuleList") List<SysAclModule>  sysAclModuleList);

    int countByIdAndNameAndParentId(@Param("id") Integer id, @Param("name") String name, @Param("parentId") int parentId );
}
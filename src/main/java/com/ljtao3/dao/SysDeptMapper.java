package com.ljtao3.dao;

import com.ljtao3.model.SysAclModule;
import com.ljtao3.model.SysDept;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysDeptMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysDept record);

    int insertSelective(SysDept record);

    SysDept selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysDept record);

    int updateByPrimaryKey(SysDept record);

    List<SysDept> getAllDept();

    //查询某层级下的所有子部门
    List<SysDept> getChildDeptListByLevel(String level);

    void batchUpdateLevel(@Param("sysDeptList") List<SysDept>  sysDeptList);

    int countByIdAndNameAndParentId(@Param("id") Integer id,@Param("name") String name,@Param("parentId") int parentId );

}
package com.ljtao3.dao;

import com.ljtao3.model.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysRole record);

    int insertSelective(SysRole record);

    SysRole selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysRole record);

    int updateByPrimaryKey(SysRole record);

    List<SysRole> getAllRole();

    int countByName(@Param("id") Integer id,@Param("name") String name);

    List<SysRole> getByIdList(@Param("idList") List<Integer> idList);
}
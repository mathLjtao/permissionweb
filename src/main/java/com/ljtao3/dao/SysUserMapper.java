package com.ljtao3.dao;

import com.ljtao3.beans.PageQuery;
import com.ljtao3.model.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysUser record);

    int insertSelective(SysUser record);

    SysUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysUser record);

    int updateByPrimaryKey(SysUser record);

    SysUser findByKeyword(@Param("keyword") String keyword);

    int countByMail(@Param("mail") String mail);

    int countByTelephone(@Param("phone") String phone);

    int countByDeptId(@Param("deptId") Integer deptId);

    List<SysUser> getPageByDeptId(@Param("deptId") Integer deptId, @Param("page") PageQuery page);
}
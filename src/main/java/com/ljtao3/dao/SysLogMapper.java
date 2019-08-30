package com.ljtao3.dao;

import com.ljtao3.beans.PageQuery;
import com.ljtao3.model.SysLog;
import com.ljtao3.model.SysLogWithBLOBs;
import com.ljtao3.param.LogParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysLogWithBLOBs record);

    int insertSelective(SysLogWithBLOBs record);

    SysLogWithBLOBs selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysLogWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(SysLogWithBLOBs record);

    int updateByPrimaryKey(SysLog record);

    List<SysLogWithBLOBs> getAll();

    int getCount();

    List<SysLogWithBLOBs> getByCondWithPage(@Param("param") LogParam param,@Param("page") PageQuery page);

    int getCountByCond(@Param("param") LogParam param);
}
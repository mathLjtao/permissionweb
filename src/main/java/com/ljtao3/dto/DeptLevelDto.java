package com.ljtao3.dto;

import com.google.common.collect.Lists;
import com.ljtao3.model.SysDept;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Getter
@Setter
@ToString
public class DeptLevelDto extends SysDept {
    private List<DeptLevelDto> deptLevelDtoList= Lists.newArrayList();
    public static DeptLevelDto adapt(SysDept dept){
        DeptLevelDto dto=new DeptLevelDto();
        BeanUtils.copyProperties(dept,dto);
        return dto;
    }
}

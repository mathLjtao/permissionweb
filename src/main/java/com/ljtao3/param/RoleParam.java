package com.ljtao3.param;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RoleParam {
    private Integer id;

    private String name;

    private Integer type;

    private Integer status;

    private String remark;
}

package com.ljtao3.param;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AclModuleParam {
    private Integer id;

    private String name;

    private Integer parentId=0;

    private String level;

    private Integer seq;

    private Integer status;

    private String remark;
}

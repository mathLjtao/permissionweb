package com.ljtao3.param;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AclParam {
    private Integer id;

    private String name;

    private Integer aclModuleId;

    private String url;

    private Integer type;

    private Integer status;

    private Integer seq;

    private String remark;
}

package com.ljtao3.param;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DeptParam {
    private Integer id;
    private String name;
    private Integer parentId=0;
    //展示顺序
    private Integer seq;
    //备注
    private String remark;
}

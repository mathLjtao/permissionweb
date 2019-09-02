package com.ljtao3.param;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchLogParam {
    String beforeSeg;
    String afterSeg;
    String operator;
    String fromTime;
    String toTime;
    Integer type;
}

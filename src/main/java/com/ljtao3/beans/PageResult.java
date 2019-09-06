package com.ljtao3.beans;

import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
@Builder
public class PageResult<T> {

    private int total=0;
    private List<T> data = Lists.newArrayList();

    public PageResult(int total,List<T> data){
        this.total=total;
        this.data=data;
    }
    public PageResult(){}

}

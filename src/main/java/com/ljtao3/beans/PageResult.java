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
public class PageResult<T> {
    private List<T> data=new ArrayList<>();
    private int total=0;
    public PageResult(int total,List<T> data){
        this.total=total;
        this.data=data;
    }
    public PageResult(){}
}

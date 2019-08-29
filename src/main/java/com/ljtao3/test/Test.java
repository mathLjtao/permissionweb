package com.ljtao3.test;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.ljtao3.beans.CacheKeyConstants;
import com.ljtao3.model.SysAcl;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class Test {
    public static void main(String[] args) throws  Exception{
        CacheKeyConstants[] values = CacheKeyConstants.values();
        String key=values[0].name();
        String[] keys=new String[]{"dddd_aa","ccc_ddd"};

        if(keys!=null && keys.length>0){
            key += "_"+ Joiner.on("_").join(keys);
        }
       System.out.println(key);
    }
    //4个字之后就加上<br>
    public static String addBr(String str){
        StringBuffer sb=new StringBuffer();
        int len=str.length();
        for (int i = 0; i < len; i=i+4) {
            sb.append(str.substring(i,i+4>=len?len:i+4));
            sb.append("<br>");
        }

        return sb.toString();
    }
}

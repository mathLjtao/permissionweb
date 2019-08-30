package com.ljtao3.test;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.ljtao3.beans.CacheKeyConstants;
import com.ljtao3.model.SysAcl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class Test {
    public static void main(String[] args) throws  Exception{
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse = simpleDateFormat.parse("2010-81-30 14:12:44");
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

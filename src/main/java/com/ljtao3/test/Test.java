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
        flowRule();

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
    //流程委托规则的判断
    public static void flowRule() throws  Exception{
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse = simpleDateFormat.parse("2010-81-30 14:12:44");//2008-01-31 00:00:00
        Date date=new Date();
        Date endDate;
        Date beginDate;
        //String beginDateStr="0000-00-00 00:00:00";String endDateStr="0000-00-00 00:00:00";
        //String beginDateStr="2010-00-00 00:00:00";String endDateStr="2020-00-00 00:00:00";
        String beginDateStr="2010-00-00 00:00:00";String endDateStr=null;
        //结束时间的判断
        if(endDateStr==null || "0000-00-00 00:00:00".equals(endDateStr)){
            System.out.println("1.....");
            return;
        }
        if(beginDateStr==null || "0000-00-00 00:00:00".equals(beginDateStr)){
            beginDate=new Date(-1);
        }
        else{
            beginDate=simpleDateFormat.parse(beginDateStr);
        }
        endDate=simpleDateFormat.parse(endDateStr);

        //开始时间为0，结束时间不为0的判断
        if(beginDate.getTime()==-1 && (endDate.getTime()-date.getTime()>=0)){
            System.out.println("2.....");
            return;
        }
        //开始时间跟结束时间不为0的判断
        if((beginDate.getTime()-date.getTime()<=0) && (endDate.getTime()-date.getTime()>=0)){
            System.out.println("3.....");
            return;
        }
    }
}

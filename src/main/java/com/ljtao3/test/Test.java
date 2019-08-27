package com.ljtao3.test;

import com.ljtao3.model.SysAcl;

import java.util.Date;

public class Test {
    public static void main(String[] args) throws  Exception{


        System.out.println(String.valueOf(new Date().getTime()));

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

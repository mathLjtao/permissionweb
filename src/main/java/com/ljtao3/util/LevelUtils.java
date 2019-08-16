package com.ljtao3.util;

import org.apache.commons.lang3.StringUtils;

public class LevelUtils {
    public final  static String SEPARATOR =".";
    public final static String ROOT="0";
    public static String calculateLevel(String parentLevel,int parentId){
        if(StringUtils.isBlank(parentLevel)){
            return ROOT;
        }
        else{
            return StringUtils.join(parentLevel,SEPARATOR,parentId);
        }
    }
}

package com.ljtao3.common;

import java.util.HashMap;
import java.util.Map;

public class JsonData {
    private boolean ret;
    private String msg;
    private Object data;
    public JsonData (boolean ret){
        this.ret=ret;
    }
    public static JsonData success(String msg,Object data){
        JsonData jd=new JsonData(true);
        jd.data=data;
        jd.msg=msg;
        return jd;
    }
    public static JsonData success(Object data){
        JsonData jd=new JsonData(true);
        jd.data=data;
        return jd;
    }
    public static JsonData success(){
        return new JsonData(true);
    }
    public static JsonData fail(String msg){
        JsonData jd=new JsonData(false);
        jd.msg=msg;
        return jd;
    }
    public Map<String,Object> toMap(){
        HashMap<String,Object> result=new HashMap<>();
        result.put("ret",ret);
        result.put("msg",msg);
        result.put("data",data);
        return result;
    }

    public boolean isRet() {
        return ret;
    }

    public void setRet(boolean ret) {
        this.ret = ret;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

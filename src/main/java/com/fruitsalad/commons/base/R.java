package com.fruitsalad.commons.base;

import java.io.Serializable;

public class R implements Serializable {
    private static final long serialVersionUID = 4451643575228858578L;
    private static final String ERROR = "0";
    private static final String OK = "1";
    private String code;
    private String msg;
    private Object result;

    public R() {
    }

    public static R ok(){
        R r = new R();
        r.code = OK;
        r.msg = "成功";
        return r;
    }
    public static R ok(Object result){
        R r = R.ok();
        r.result = result;
        return r;
    }

    public static R ok(String msg, Object result){
        R r = R.ok();
        r.msg = msg;
        r.result = result;
        return r;
    }

    public static R error(){
        R r = new R();
        r.code = ERROR;
        r.msg = "失败";
        return r;
    }

    public static R error(String msg){
        R r = R.error();
        r.msg = msg;
        return r;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}

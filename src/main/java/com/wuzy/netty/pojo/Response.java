package com.wuzy.netty.pojo;

import java.io.Serializable;

/**
 * Created by wuzhengyun on 16/7/11.
 */
public class Response implements Serializable{
    private String msg;

    public Response(){}

    public Response(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

package com.wuzy.netty.pojo;

import java.io.Serializable;

/**
 * Created by wuzhengyun on 16/7/11.
 */
public class Request implements Serializable{
    private int id;
    private String name;

    public Request(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

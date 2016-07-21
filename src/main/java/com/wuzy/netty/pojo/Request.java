package com.wuzy.netty.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wuzhengyun on 16/7/11.
 */
public class Request extends MsgType implements Serializable {
    private int id;
    private String name;
    private String pwd;//密码
    private String uname;//用户名
    private List<String> friends;
    private String toUser;//向谁发送消息
    private String msg;//发送的消息



    public Request() {
    }

    public Request(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
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

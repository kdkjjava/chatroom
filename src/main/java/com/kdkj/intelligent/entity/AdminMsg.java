package com.kdkj.intelligent.entity;

/**
 * powered by IntelliJ IDEA
 *
 * @Author: unknown
 * @Date: 2018/1/16 18:36
 * @Description:
 **/
public class AdminMsg {

    private String msgFrom;

    private String admin;

    private String msg;

    private String date;

    private Boolean read;

    private String title;

    public String getTitle() {
        return title;
    }

    public AdminMsg setTitle(String title) {
        this.title = title;
        return this;
    }

    public Boolean getRead() {
        return read;
    }

    public AdminMsg setRead(Boolean read) {
        this.read = read;
        return this;
    }

    public String getDate() {
        return date;
    }

    public AdminMsg setDate(String date) {
        this.date = date;
        return this;
    }

    public String getMsgFrom() {
        return msgFrom;
    }

    public AdminMsg setMsgFrom(String msgFrom) {
        this.msgFrom = msgFrom;
        return this;
    }

    public String getAdmin() {
        return admin;
    }

    public AdminMsg setAdmin(String admin) {
        this.admin = admin;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public AdminMsg setMsg(String msg) {
        this.msg = msg;
        return this;
    }
}

package com.kdkj.intelligent.entity;

import java.util.Date;

/**
 * powered by IntelliJ IDEA
 *
 * @Author: unknown
 * @Date: 2018/1/16 18:36
 * @Description:
 **/
public class AdminMsg {

    private static int increment;

    private String msgFrom;

    private String admin;

    private String msg;

    private Date date;

    private Boolean read;

    private String title;

    private int id;

    public AdminMsg(){
        setId(++increment);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public Date getDate() {
        return date;
    }

    public AdminMsg setDate(Date date) {
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

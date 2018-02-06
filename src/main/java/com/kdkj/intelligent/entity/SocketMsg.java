package com.kdkj.intelligent.entity;

/**
 * powered by IntelliJ IDEA
 *
 * @Author: unknown
 * @Date: 2018/1/12 15:30
 * @Description:
 **/
public class SocketMsg {

    private String msg;

    private boolean status;

    private String groupId;

    private String msgFrom;

    private String msgTo;

    private String date;

    private String masterName;

    private String binary;

    private int number;

    private String pictureAddress;

    public String getPictureAddress() {
        return pictureAddress;
    }

    public void setPictureAddress(String pictureAddress) {
        this.pictureAddress = pictureAddress;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getBinary() {
        return binary;
    }

    public void setBinary(String binary) {
        this.binary = binary;
    }

    public String getMasterName() {
        return masterName;
    }

    public SocketMsg setMasterName(String masterName) {
        this.masterName = masterName;
        return this;
    }

    public String getDate() {
        return date;
    }

    public SocketMsg setDate(String date) {
        this.date = date;
        return this;
    }

    public SocketMsg() {
    }

    public String getMsgFrom() {
        return msgFrom;
    }

    public SocketMsg setMsgFrom(String msgFrom) {
        this.msgFrom = msgFrom;
        return this;
    }

    public String getMsgTo() {
        return msgTo;
    }

    public SocketMsg setMsgTo(String msgTo) {
        this.msgTo = msgTo;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public SocketMsg setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public boolean getStatus() {
        return status;
    }

    public SocketMsg setStatus(boolean status) {
        this.status = status;
        return this;
    }

    public String getGroupId() {
        return groupId;
    }

    public SocketMsg setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

}

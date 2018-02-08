package com.kdkj.intelligent.entity;

/**
 * powered by IntelliJ IDEA
 *
 * @Author: unknown
 * @Date: 2018/1/18 17:23
 * @Description:
 **/
public class TipsMsg {

    private String msgFrom;

    private String msgType;

    private Integer count;

    private String groupId;

    public String getGroupId() {
        return groupId;
    }

    public TipsMsg setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public String getMsgFrom() {
        return msgFrom;
    }

    public TipsMsg setMsgFrom(String msgFrom) {
        this.msgFrom = msgFrom;
        return this;
    }

    public String getMsgType() {
        return msgType;
    }

    public TipsMsg setMsgType(String msgType) {
        this.msgType = msgType;
        return this;
    }

    public Integer getCount() {
        return count;
    }

    public TipsMsg setCount(Integer count) {
        this.count = count;
        return this;
    }
}

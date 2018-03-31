package com.kdkj.intelligent.entity;

import java.io.Serializable;
import java.util.Date;

public class Members extends Pageinfo implements Serializable{
    private Long id;
    /*
	 * 用户id
	 */
    private Integer userId;
    /*
	 * 群id
	 */
    private Integer groupId;
    /*
	 * 当前用户在群中是否能玩游戏
	 */
    private String status;
    /*
	 * 加群时间
	 */
    private Date buildTime;

    private Integer block;

    private Integer ban;

    private Date blockTime;

    public Date getBlockTime() {
        return blockTime;
    }

    public void setBlockTime(Date blockTime) {
        this.blockTime = blockTime;
    }

    public Integer getBlock() {
        return block;
    }

    public void setBlock(Integer block) {
        this.block = block;
    }

    public Integer getBan() {
        return ban;
    }

    public void setBan(Integer ban) {
        this.ban = ban;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Date getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(Date buildTime) {
        this.buildTime = buildTime;
    }
}
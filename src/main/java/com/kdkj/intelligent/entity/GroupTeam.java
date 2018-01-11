package com.kdkj.intelligent.entity;

import java.io.Serializable;
import java.util.Date;

public class GroupTeam extends Pageinfo implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -5292702707857401694L;
	private Integer id;
    /*
	 * 群号
	 */
    private String groupId;
    /*
	 * 群名称
	 */
    private String groupName;
    /*
	 * 群主账号
	 */
    private Integer masterId;
    /*
	 * 群等级？
	 */
    private Integer upperLimit;
    /*
	 * 群状态
	 */
    private String status;
    /*
	 * 群类型
	 */
    private String type;
    /*
	 * 创建时间
	 */
    private Date buildTime;
    /*
	 * 游戏id
	 */
    private Integer gameId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId == null ? null : groupId.trim();
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName == null ? null : groupName.trim();
    }

    public Integer getMasterId() {
        return masterId;
    }

    public void setMasterId(Integer masterId) {
        this.masterId = masterId;
    }

    public Integer getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(Integer upperLimit) {
        this.upperLimit = upperLimit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Date getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(Date buildTime) {
        this.buildTime = buildTime;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }
}
package com.kdkj.intelligent.entity;

import java.io.Serializable;
import java.util.Date;

public class Users extends Pageinfo implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -8543965219359179064L;

	private Integer id;
	/*
	 * 用户名
	 */
    private String username;

    private String password;
    /*
	 * 昵称
	 */
    private String nickname;
    /*
	 * 用户类型 0-普通用户 1-代理商 2-管理员
	 */
    private String type;
    /*
	 * 状态
	 */
    private String status;
    /*
	 * 积分
	 */
    private Long score;
    /*
	 * 注册时间
	 */
    private Date registTime;
    /*
	 * 头像
	 */
    private String pictureAddress;
    /*
	 * 电话
	 */
    private String phone;

	/*
	 * token
	 */
    private String token;
    /*
	 * 最后登录时间
	 */
    private Date lastLoginTime;
    
    /*
     * 无群时间
     */
    private Date nogroupTime;

    /**
     * 用户等级
     */
    private String level;

    private String proxyLevel;
    /**
     * 过期时间
     */
    private Date expireDate;
    /**
     * 代理商
     */
    private String master;
    /**
     * 备注字段1
     */
    private String remark1;
    /**
     * 备注字段2
     */
    private String remark2;
    /**
     * 备注字段3
     */
    private Date remark3;
    
    public String getMaster() {
		return master;
	}

	public void setMaster(String master) {
		this.master = master;
	}

	public String getRemark1() {
		return remark1;
	}

	public void setRemark1(String remark1) {
		this.remark1 = remark1;
	}

	public String getRemark2() {
		return remark2;
	}

	public void setRemark2(String remark2) {
		this.remark2 = remark2;
	}

	public Date getRemark3() {
		return remark3;
	}

	public void setRemark3(Date remark3) {
		this.remark3 = remark3;
	}

    public String getProxyLevel() {
        return proxyLevel;
    }

    public void setProxyLevel(String proxyLevel) {
        this.proxyLevel = proxyLevel;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Date getNogroupTime() {
		return nogroupTime;
	}

	public void setNogroupTime(Date nogroupTime) {
		this.nogroupTime = nogroupTime;
	}

    
    public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname == null ? null : nickname.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public Date getRegistTime() {
        return registTime;
    }

    public void setRegistTime(Date registTime) {
        this.registTime = registTime;
    }

    public String getPictureAddress() {
        return pictureAddress;
    }

    public void setPictureAddress(String pictureAddress) {
        this.pictureAddress = pictureAddress == null ? null : pictureAddress.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", score=" + score +
                ", registTime=" + registTime +
                ", pictureAddress='" + pictureAddress + '\'' +
                ", phone='" + phone + '\'' +
                ", token='" + token + '\'' +
                ", lastLoginTime=" + lastLoginTime +
                ", nogroupTime=" + nogroupTime +
                ", level='" + level + '\'' +
                '}';
    }
}
package com.kdkj.intelligent.entity;

import java.io.Serializable;
import java.util.Date;

public class Friendship extends Pageinfo implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 6007427632004714139L;

	private Long id;

    private Integer uid1;

    private Integer uid2;

    private Date buildTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUid1() {
        return uid1;
    }

    public void setUid1(Integer uid1) {
        this.uid1 = uid1;
    }

    public Integer getUid2() {
        return uid2;
    }

    public void setUid2(Integer uid2) {
        this.uid2 = uid2;
    }

    public Date getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(Date buildTime) {
        this.buildTime = buildTime;
    }
}
package com.kdkj.intelligent.entity;

public class Pageinfo {
	private int current = 1;
	private int pageNum;
	private int pageSize = 10;
	private String orderBy;

	public int getCurrent() {
		return current;
	}
	public void setCurrent(int current) {
		this.current = current;
		pageNum = (current-1)*pageSize;
	}
	public int getPageNum() {
		return pageNum;
	}
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;

	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	

}

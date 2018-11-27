package com.seezoon.boot.context.dto;

/**
 * 后台用户线程上下文对象
 * @author hdf
 *
 */
public class AdminUser {

	/**
	 * 用户id
	 */
	private String userId;
	/**
	 * 数据权限
	 */
	private String dsf;

	public AdminUser() {
		super();
	}

	public AdminUser(String userId,String dsf) {
		super();
		this.userId = userId;
		this.dsf = dsf;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDsf() {
		return dsf;
	}

	public void setDsf(String dsf) {
		this.dsf = dsf;
	}
	
}

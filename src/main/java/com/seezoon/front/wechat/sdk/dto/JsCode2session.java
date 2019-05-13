package com.seezoon.front.wechat.sdk.dto;

/**
 * code2session
 * @author hdf
 *
 */
public class JsCode2session extends BaseWechatDto{

	private String openid;
	/**
	 * 微信的session
	 */
	private String session_key;
	private String unionid;
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getSession_key() {
		return session_key;
	}
	public void setSession_key(String session_key) {
		this.session_key = session_key;
	}
	public String getUnionid() {
		return unionid;
	}
	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}
	
}

package com.seezoon.front.wechat.sdk.dto;

public class WxRefundCallback extends WxTransactionRespone {

	private String appid;
	private String mch_id;
	private String nonce_str;
	private String req_info;

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getMch_id() {
		return mch_id;
	}

	public void setMch_id(String mch_id) {
		this.mch_id = mch_id;
	}

	public String getNonce_str() {
		return nonce_str;
	}

	public void setNonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}

	public String getReq_info() {
		return req_info;
	}

	public void setReq_info(String req_info) {
		this.req_info = req_info;
	}

}

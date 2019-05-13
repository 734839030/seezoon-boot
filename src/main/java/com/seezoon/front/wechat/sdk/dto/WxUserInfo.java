package com.seezoon.front.wechat.sdk.dto;

public class WxUserInfo {

	private String openId;
	private String nickName;
	private String gender;
	private String city;
	private String province;
	private String country;
	private String avatarUrl;
	private String unionId;
	private Watermark watermark;
	
	public Watermark getWatermark() {
		return watermark;
	}


	public void setWatermark(Watermark watermark) {
		this.watermark = watermark;
	}


	public String getOpenId() {
		return openId;
	}


	public void setOpenId(String openId) {
		this.openId = openId;
	}


	public String getNickName() {
		return nickName;
	}


	public void setNickName(String nickName) {
		this.nickName = nickName;
	}


	public String getGender() {
		return gender;
	}


	public void setGender(String gender) {
		this.gender = gender;
	}


	public String getCity() {
		return city;
	}


	public void setCity(String city) {
		this.city = city;
	}


	public String getProvince() {
		return province;
	}


	public void setProvince(String province) {
		this.province = province;
	}


	public String getCountry() {
		return country;
	}


	public void setCountry(String country) {
		this.country = country;
	}


	public String getAvatarUrl() {
		return avatarUrl;
	}


	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}


	public String getUnionId() {
		return unionId;
	}


	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}


	public static class Watermark {
		private String appid;
		private Long timestamp;
		
		public String getAppid() {
			return appid;
		}
		public void setAppid(String appid) {
			this.appid = appid;
		}
		public Long getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(Long timestamp) {
			this.timestamp = timestamp;
		}
		
	}
}

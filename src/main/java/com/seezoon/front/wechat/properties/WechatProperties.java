package com.seezoon.front.wechat.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 微信配置信息
 * @author hdf
 *
 */
@Configuration
@EnableConfigurationProperties(WechatProperties.class)
@ConfigurationProperties(prefix="wx")
public class WechatProperties {

	private MPProperties mp;
	private PayProperties pay;
	private MsgTemplate mt;
	
	public MsgTemplate getMt() {
		return mt;
	}
	public void setMt(MsgTemplate mt) {
		this.mt = mt;
	}
	public PayProperties getPay() {
		return pay;
	}
	public void setPay(PayProperties pay) {
		this.pay = pay;
	}
	public MPProperties getMp() {
		return mp;
	}
	public void setMp(MPProperties mp) {
		this.mp = mp;
	}
	/**
	 * 公众号配置参数
	 * @author hdf
	 *
	 */
	public static class CPProperties {
		
	}
	/**
	 * 小程序配置参数
	 * @author hdf
	 *
	 */
	public static class MPProperties {
		private String appid;
		private String appsecret;
		public String getAppid() {
			return appid;
		}
		public void setAppid(String appid) {
			this.appid = appid;
		}
		public String getAppsecret() {
			return appsecret;
		}
		public void setAppsecret(String appsecret) {
			this.appsecret = appsecret;
		}
	}
	public static class MsgTemplate {
		private String paiedMsg;
		private String useMsg;
		public String getPaiedMsg() {
			return paiedMsg;
		}
		public void setPaiedMsg(String paiedMsg) {
			this.paiedMsg = paiedMsg;
		}
		public String getUseMsg() {
			return useMsg;
		}
		public void setUseMsg(String useMsg) {
			this.useMsg = useMsg;
		}
		
	}
	public static class PayProperties {
		private String mchId;
		private String mchKey;
		private String notifyUrl;
		private String refundUrl;
		private String spbillCreateIp;
		public String getMchId() {
			return mchId;
		}
		public void setMchId(String mchId) {
			this.mchId = mchId;
		}
		public String getMchKey() {
			return mchKey;
		}
		public void setMchKey(String mchKey) {
			this.mchKey = mchKey;
		}
		public String getNotifyUrl() {
			return notifyUrl;
		}
		public void setNotifyUrl(String notifyUrl) {
			this.notifyUrl = notifyUrl;
		}
		public String getSpbillCreateIp() {
			return spbillCreateIp;
		}
		public void setSpbillCreateIp(String spbillCreateIp) {
			this.spbillCreateIp = spbillCreateIp;
		}

		public String getRefundUrl() {
			return refundUrl;
		}

		public void setRefundUrl(String refundUrl) {
			this.refundUrl = refundUrl;
		}
	}
}

package com.seezoon.boot.common.file.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *  参见阿里云oss 
	file.aliyun.urlPrefix=https://seezoon-file.oss-cn-hangzhou.aliyuncs.com
	file.aliyun.bucketName=seezoon-file
	file.aliyun.endpoint=oss-cn-hangzhou.aliyuncs.com
	file.aliyun.accessKeyId=LTAIe6QxGGGrTSSe
	file.aliyun.accessKeySecret=xxxxxxxxxxxxx
 * @author hdf
 *
 */
@ConfigurationProperties(prefix = "file.aliyun")
public class AliOssProperties {
	
	private String urlPrefix;
	private String bucketName;
	private String endpoint;
	private String accessKeyId;
	private String accessKeySecret;

	public String getUrlPrefix() {
		return urlPrefix;
	}

	public void setUrlPrefix(String urlPrefix) {
		this.urlPrefix = urlPrefix;
	}


	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getAccessKeyId() {
		return accessKeyId;
	}

	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}

	public String getAccessKeySecret() {
		return accessKeySecret;
	}

	public void setAccessKeySecret(String accessKeySecret) {
		this.accessKeySecret = accessKeySecret;
	}

}

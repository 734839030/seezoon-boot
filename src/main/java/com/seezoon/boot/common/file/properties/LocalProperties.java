package com.seezoon.boot.common.file.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 
 * @author hdf
 *	file.local.urlPrefix=http://127.0.0.1:8888/images
	file.local.storePath=/Users/hdf/Documents/develop/projects/sts2/seezoon-boot/static/dist/images
 */
@ConfigurationProperties(prefix = "file.local")
public class LocalProperties {
	private String urlPrefix;
	private String storePath;
	public String getUrlPrefix() {
		return urlPrefix;
	}
	public void setUrlPrefix(String urlPrefix) {
		this.urlPrefix = urlPrefix;
	}
	public String getStorePath() {
		return storePath;
	}
	public void setStorePath(String storePath) {
		this.storePath = storePath;
	}

}

package com.seezoon.boot.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.seezoon.boot.common.file.handler.AliFileFileHandler;
import com.seezoon.boot.common.file.handler.FileHandler;
import com.seezoon.boot.common.file.handler.LocalFileHandler;
import com.seezoon.boot.common.file.properties.AliOssProperties;
import com.seezoon.boot.common.file.properties.LocalProperties;

/**
 * 上传文件handler 配置
 * @author hdf
 *
 */
@Configuration
@EnableConfigurationProperties(value= {AliOssProperties.class,LocalProperties.class})
public class FileHandlerBeanAutoConfiguration {

	@Primary
	@Bean(initMethod="init")
	@ConditionalOnProperty(name="file.storage",havingValue="local")
	public FileHandler localFileHandler() {
		return new LocalFileHandler(); 
	}
	
	@Bean(initMethod="init")
	@ConditionalOnProperty(name="file.storage",havingValue="aliyun")
	public FileHandler aliFileFileHandler() {
		return new AliFileFileHandler();
	}
}

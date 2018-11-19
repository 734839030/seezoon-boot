package com.seezoon.boot.configuration;

import java.nio.charset.StandardCharsets;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateAutoConfiguration {

	public static int DEFAULT_READ_TIMEOUT = 5000;
	public static int DEFAULT_CONNECT_TIMEOUT = 5000;
	@Bean
	public  RestTemplate restTemplate() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory(); 
		factory.setReadTimeout(DEFAULT_READ_TIMEOUT);
		factory.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
		RestTemplate restTemplate = new RestTemplate(factory);
		restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
		return null;
	}
}

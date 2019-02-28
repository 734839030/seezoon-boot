package com.seezoon.boot.configuration;

import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.seezoon.boot.common.http.HttpClientConfig;
import com.seezoon.boot.common.http.HttpClientIdleConnectionMonitor;
import com.seezoon.boot.common.http.SkipSslVerificationHttpRequestFactory;

@Configuration
public class RestTemplateAutoConfiguration {

	@Autowired
	private HttpClientConfig httpClientConfig;
	
	@Bean("nonePoolRestTemplate")
	public RestTemplate nonePoolRestTemplate() {
		RestTemplate nonePoolRestTemplate = new RestTemplate();
		SkipSslVerificationHttpRequestFactory skipSslVerificationHttpRequestFactory = new SkipSslVerificationHttpRequestFactory();
		skipSslVerificationHttpRequestFactory.setConnectTimeout(httpClientConfig.getConnectTimeout());
		skipSslVerificationHttpRequestFactory.setReadTimeout(httpClientConfig.getSocketTimeout());
		nonePoolRestTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
		return nonePoolRestTemplate;
	}
	@Primary
	@Bean
	public  RestTemplate restTemplate() {
		ClientHttpRequestFactory factory = clientHttpRequestFactory(); 
		RestTemplate restTemplate = new RestTemplate(factory);
		restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
		return restTemplate;
	}
	@Bean
    public HttpClientConnectionManager poolingConnectionManager() {
		SSLContext sslContext = null;
		try {
			sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return false;
				}
			}).build();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext,
				NoopHostnameVerifier.INSTANCE);
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sslSocketFactory)
				.build();
		PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		poolingConnectionManager.setMaxTotal(httpClientConfig.getMaxTotal()); // 连接池最大连接数  
		poolingConnectionManager.setDefaultMaxPerRoute(httpClientConfig.getMaxPerRoute()); // 每个主机的并发
		// 长连接
		poolingConnectionManager.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(httpClientConfig.getSocketTimeout()).setSoKeepAlive(true).build());
				// 连接不活跃多久检查毫秒 并不是100 % 可信
		poolingConnectionManager.setValidateAfterInactivity(httpClientConfig.getValidateAfterInactivity());
		// 空闲扫描线程
		HttpClientIdleConnectionMonitor.registerConnectionManager(poolingConnectionManager, httpClientConfig);
		return poolingConnectionManager;
    }
    
    @Bean
    public HttpClientBuilder httpClientBuilder() {
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
	    //设置HTTP连接管理器
		httpClientBuilder.setConnectionManager(poolingConnectionManager());
		RequestConfig requestConfig = RequestConfig.custom()
				//.setProxy(proxy)
				.setConnectionRequestTimeout(httpClientConfig.getConnectionRequestTimeout())// 获取连接等待时间
				.setConnectTimeout(httpClientConfig.getConnectTimeout())// 连接超时
				.setSocketTimeout(httpClientConfig.getSocketTimeout())// 获取数据超时
				.build();
		httpClientBuilder.setDefaultRequestConfig(requestConfig)
		.setUserAgent(httpClientConfig.getUserAgent())
		.disableContentCompression().disableAutomaticRetries()
		.setConnectionTimeToLive(httpClientConfig.getConnTimeToLive(), TimeUnit.MILLISECONDS)// 连接最大存活时间
		.setRetryHandler(new DefaultHttpRequestRetryHandler(httpClientConfig.getRetyTimes(), true));// 重试次数
		return httpClientBuilder;
    }
    
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() { 
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		clientHttpRequestFactory.setHttpClient(httpClientBuilder().build());
		return clientHttpRequestFactory;
    }
}

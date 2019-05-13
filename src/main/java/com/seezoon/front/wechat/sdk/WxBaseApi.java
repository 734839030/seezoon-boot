package com.seezoon.front.wechat.sdk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.seezoon.boot.common.service.BaseService;

public abstract class WxBaseApi extends BaseService{

	@Autowired
	@Qualifier("nonePoolRestTemplate")
	protected RestTemplate restTemplate;
	
	protected MediaType MEDIATYPE_APLICATION_XML_UTF8 = MediaType.parseMediaType("application/json;charset=UTF-8");
}

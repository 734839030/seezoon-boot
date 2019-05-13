package com.seezoon.front.wechat.sdk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Maps;
import com.seezoon.boot.context.exception.ServiceException;
import com.seezoon.front.wechat.sdk.dto.BaseWechatDto;

@Service
public class WxMsgApi extends WxBaseApi{
	
	@Autowired
	private WxMpUserApi wxMpUserApi;
	
	@Async
	public void sendTemplateMsg(String toUser,String templateId,String page,String form_id,String emphasisKeyword,List<String> data,String mappId,String mappsecret ) {
		Assert.hasLength(toUser,"toUser为空");
		Assert.hasLength(templateId,"templateId为空");
		Assert.hasLength(form_id,"form_id为空");
		Map<String,Object> requestData = new HashMap<String,Object>();
		requestData.put("touser", toUser);
		requestData.put("template_id", templateId);
		requestData.put("page", page);
		requestData.put("form_id", form_id);
		requestData.put("emphasis_keyword", emphasisKeyword);
		JSONObject dataArray = new JSONObject();
		if (null != data) {
			for (int i=0;i<data.size();i++) {
				Map<String,Object> temp = Maps.newHashMap();
				temp.put("value",data.get(i));
				dataArray.put("keyword" + (i+1), temp);
			}
		}
		requestData.put("data", dataArray);
		String msg = JSON.toJSONString(requestData);
		logger.debug("sendTemplateMsg:{}",msg);
		//微信接口很怪异，restTemplate 发送requestData 会序列化，但是不符合微信口味，且微信返回的数据业务json头
	    String result = restTemplate.postForObject("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + wxMpUserApi.getMpToken(mappId, mappsecret) , msg, String.class);
	    BaseWechatDto baseWechatDto = JSON.parseObject(result,BaseWechatDto.class);
	    if (!baseWechatDto.isSuccess()) {
	    	throw new ServiceException(baseWechatDto.getErrcode() + baseWechatDto.getErrmsg());
	    }
	}
}

package com.seezoon.front.wechat.sdk;

import java.nio.charset.Charset;
import java.security.AlgorithmParameters;
import java.security.Security;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.seezoon.boot.common.http.HttpRequestUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.seezoon.boot.common.http.UrlBuilder;
import com.seezoon.boot.common.utils.CodecUtils;
import com.seezoon.boot.context.exception.ServiceException;
import com.seezoon.front.wechat.sdk.dto.JsCode2session;
import com.seezoon.front.wechat.sdk.dto.Token;
import com.seezoon.front.wechat.sdk.dto.WxPhoneData;
import com.seezoon.front.wechat.sdk.dto.WxUserInfo;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class WxMpUserApi extends WxBaseApi{
	
	
	@Resource(name="redisTemplate")
	private ValueOperations<String, String> valueOperations;
	
	private BouncyCastleProvider bouncyCastleProvider = new BouncyCastleProvider();
	/**
	 * 小程序登录获取
	 * @param code
	 * @return
	 */
	public JsCode2session jscode2session(String code,String mpappid,String mpappsecret) {
		Assert.hasLength(code,"code is empty");
		Assert.hasLength(mpappid,"mpappid is empty");
		Assert.hasLength(mpappsecret,"mpappsecret is empty");
		Map<String,String> params = Maps.newHashMap();
		params.put("appid",mpappid);
		params.put("secret",mpappsecret);
		params.put("js_code",code);
		params.put("grant_type","authorization_code");
		String result = restTemplate.getForObject(UrlBuilder.build("https://api.weixin.qq.com/sns/jscode2session",params), String.class);
		JsCode2session jsCode2session = JSON.parseObject(result, JsCode2session.class);
		if (jsCode2session.isSuccess())  {
			return jsCode2session;
		} else {
			throw new ServiceException(jsCode2session.getErrmsg());
		}
	}
	
	public <T> T  parseEncryptedData(String encryptedData,String sessionkey,String iv ,Class<T> clazz) {
		logger.debug("encryptedData:{},sessionkey:{},iv:{}",encryptedData,sessionkey,iv);
        // 被加密的数据
        byte[] dataByte = CodecUtils.base64Decode(encryptedData);
        // 加密秘钥
        byte[] keyByte = CodecUtils.base64Decode(sessionkey); 
        // 偏移量
        byte[] ivByte = CodecUtils.base64Decode(iv); 
        try {
               // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // 初始化
            Security.addProvider(bouncyCastleProvider);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding","BC");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                String result = new String(resultByte, "UTF-8");
                T t = JSONObject.parseObject(result,clazz);
            	logger.debug("encryptedData userinfo :{}",result);
                return t;
            }
            throw new ServiceException("parse encryptedData error");
        } catch (Exception e) {
            throw new ServiceException(e);
        } 
	}
	
	/**
     * 获取信息
     */
    public WxUserInfo getUserInfo(String encryptedData,String sessionkey,String iv){
    	return this.parseEncryptedData(encryptedData, sessionkey, iv, WxUserInfo.class);
    }
	/**
     * 手机
     */
    public WxPhoneData getMobile(String encryptedData,String sessionkey,String iv){
    	return this.parseEncryptedData(encryptedData, sessionkey, iv, WxPhoneData.class);
    }
	/**
	 * 获取小程序接口操作token
	 * @return
	 */
	public String getMpToken(String mpappid,String mpappsecret) {
		Map<String,String> params = Maps.newHashMap();
		params.put("grant_type","client_credential");
		params.put("appid",mpappid);
		params.put("secret",mpappsecret);
		String key = "wx_mptoken" + mpappid;
		String cachedToken = valueOperations.get(key);
		Long expire = valueOperations.getOperations().getExpire(key, TimeUnit.SECONDS);
		if (StringUtils.isNotEmpty(cachedToken) && expire > 3600) {//缓存两小时，剩余时间小于1小时就重新拿
			return cachedToken;
		}  else {
			String result = restTemplate.getForObject(UrlBuilder.build("https://api.weixin.qq.com/cgi-bin/token",params), String.class, params);
			 Token token = JSON.parseObject(result,Token.class);
			 if (token.isSuccess())  {
				valueOperations.set(key, token.getAccess_token(),7200,TimeUnit.SECONDS);
				return token.getAccess_token();
			} else {
				throw new ServiceException(token.getErrmsg());
			}
		}
	}

	public byte[] generateQrCode(String path,String scene,String mpappid,String mpappsecret,int width){
		String mToken = this.getMpToken(mpappid,mpappsecret);
		Map<String, Object> reqCodeParams = new HashMap<>();
		reqCodeParams.put("page", path);
		if (StringUtils.isNotEmpty(scene)) {
			reqCodeParams.put("scene", scene);
		}
		reqCodeParams.put("width", width);
		reqCodeParams.put("auto_color", false);
		Map<String,Object> line_color = new HashMap<>();
		line_color.put("r", 0);
		line_color.put("g", 0);
		line_color.put("b", 0);
		reqCodeParams.put("line_color", line_color);
		ResponseEntity<byte[]> responseEntity = restTemplate.postForEntity("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + mToken, JSON.toJSONString(reqCodeParams), byte[].class);
		boolean json = responseEntity.getHeaders().getContentType().equals(MediaType.APPLICATION_JSON_UTF8);
		if (json) {
			JSONObject jsonObject = JSON.parseObject(StringUtils.toEncodedString(responseEntity.getBody(), Charset.forName("UTF-8")));
			throw  new ServiceException(jsonObject.getString("errcode") + ":" + jsonObject.getString("errmsg"));
		} else {
			byte[] bytes = responseEntity.getBody();
			return bytes;
		}
	}

	
}

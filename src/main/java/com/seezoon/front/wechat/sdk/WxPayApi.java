package com.seezoon.front.wechat.sdk;

import com.alibaba.fastjson.JSON;
import com.seezoon.boot.context.exception.ServiceException;
import com.seezoon.front.wechat.sdk.dto.*;
import com.seezoon.front.wechat.utils.WxUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.TreeMap;

/**
 * 支付类API
 * @author hdf
 *
 */
@Service
public class WxPayApi extends WxBaseApi{

	@Value("${spring.profiles.active}")
	private String profile;

	public JsPayParams jsPay(String openid,String body,String orderNo,
			Integer totalFee,String attach,String appid,
			String mchId,String spbillCreateIp,String notifyUrl,String mchKey) {
		WxPayRequest buildJsPayRequest = this.buildJsPayRequest(openid,body, orderNo, totalFee, attach, appid, mchId, spbillCreateIp, notifyUrl);
		WxPayRespone wxPayRespone = this.wxPay(buildJsPayRequest, mchKey);
		JsPayParams jsPayParams = this.getJsPayParams(wxPayRespone.getPrepay_id(), appid, mchKey);
		return jsPayParams;
	}
	
	
	/**
	 * JS统一下单参数
	 * @param body
	 * @param orderNo
	 * @param totalFee
	 * @param attach
	 * @param appid
	 * @param mchId
	 * @param spbillCreateIp
	 * @param notifyUrl
	 * @return
	 */
	private WxPayRequest buildJsPayRequest(String openid,String body,String orderNo,
			Integer totalFee,String attach,String appid,
			String mchId,String spbillCreateIp,String notifyUrl) {
		Assert.hasLength(openid,"openid is empty");
		Assert.hasLength(body,"body is empty");
		Assert.hasLength(orderNo,"orderNo is empty");
		Assert.notNull(totalFee,"totalFee is null");
		Assert.hasLength(appid,"mappid is empty");
		Assert.hasLength(mchId,"mchId is empty");
		Assert.hasLength(spbillCreateIp,"spbillCreateIp is empty");
		Assert.hasLength(notifyUrl,"notifyUrl is empty");
		WxPayRequest wxPayRequest = new WxPayRequest();
		wxPayRequest.setOpenid(openid);
		wxPayRequest.setAppid(appid);
		wxPayRequest.setMch_id(mchId);
		wxPayRequest.setNonce_str(WxUtils.createNoncestr());
		wxPayRequest.setTrade_type("JSAPI");
		wxPayRequest.setBody(body);
		wxPayRequest.setOut_trade_no(orderNo);
		wxPayRequest.setTotal_fee(totalFee);
		wxPayRequest.setSpbill_create_ip(spbillCreateIp);
		wxPayRequest.setAttach(attach);
		wxPayRequest.setNotify_url(notifyUrl);
		return wxPayRequest;
	}
	
	public WxPayResult getPayResult(String payResultXml,String mchKey) {
		Assert.hasLength(payResultXml,"验证签名数据为空");
		logger.info("payResultXml:{}",payResultXml);
		//解析数据
		WxPayResult payResult = WxUtils.xmlToBean(payResultXml, WxPayResult.class);
		if (!payResult.isSuccess()) {
			throw new ServiceException("微信支付返回支付失败");
		}
		logger.info("payResult attach:{},openid:{},out_trade_no:{},transaction_id:{},total_fee:{}",payResult.getAttach(),payResult.getOpenid(),payResult.getOut_trade_no(),payResult.getTransaction_id(),payResult.getTotal_fee());
		//验证签名 用原始数据验签名
		TreeMap<String, Object> xml2map = WxUtils.xml2map(payResultXml);
		//验证签名
		String sign = (String) xml2map.remove("sign");
		String createSign = WxUtils.sign(WxUtils.createSortStr(xml2map), mchKey);
		if (!createSign.equals(sign)) {
			logger.error("check pay sign error sign :{} createSign:{}",sign,createSign);
			throw new ServiceException("支付回调签名错误");
		}
		return payResult;
	}
	
	/**
	 * 统一下单请求微信服务器
	 * @param wxPayRequest
	 * @return
	 */
	private WxPayRespone wxPay(WxPayRequest wxPayRequest,String mchKey) {
		Assert.hasLength(mchKey,"mchKey is empty");
		String sortStr = WxUtils.createSortStr(WxUtils.bean2map(wxPayRequest));
		String sign = WxUtils.sign(sortStr, mchKey);
		wxPayRequest.setSign(sign);
		String xml = WxUtils.beanToXml(wxPayRequest);
		logger.debug("wxPay-xml:{}",xml);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MEDIATYPE_APLICATION_XML_UTF8);
		HttpEntity<String> httpEntity = new HttpEntity<>(xml, headers);
		String resultXml = restTemplate.postForObject("https://api.mch.weixin.qq.com/pay/unifiedorder", httpEntity, String.class);
		WxPayRespone wxPayRespone = WxUtils.xmlToBean(resultXml, WxPayRespone.class);
		logger.debug("wxPay-xmlRespone:{}",JSON.toJSONString(wxPayRespone));
		if (!wxPayRespone.isSuccess()) {
			throw new ServiceException(wxPayRespone.getReturn_msg() + StringUtils.trimToEmpty(wxPayRespone.getErr_code_des()));
		}
		return wxPayRespone;
	}
	
	/**
	 * 小程序和js 支付都是这样方法
	 * @param prepay_id
	 * @param appid
	 * @param mchKey
	 * @return
	 */
	private JsPayParams getJsPayParams(String prepay_id,String appid,String mchKey){
		Assert.hasLength(prepay_id,"prepay_id 为空");
		Assert.hasLength(appid,"appid 为空");
		Assert.hasLength(mchKey,"mchKey 为空");
		TreeMap<String,Object> map = new TreeMap<>();
		map.put("appId", appid);
		map.put("timeStamp", String.valueOf(WxUtils.createTimestamp()));
		map.put("nonceStr", WxUtils.createNoncestr());
		map.put("package", "prepay_id=" + prepay_id);
		map.put("signType", "MD5");
		String sign = WxUtils.sign(WxUtils.createSortStr(map), mchKey);
		map.put("paySign", sign);
		// package 为js 关键字需要转换下
		map.put("_package", "prepay_id=" + prepay_id);
		JsPayParams jsParams = new JsPayParams();
		try {
			BeanUtils.populate(jsParams, map);
		} catch (Exception e) {
			logger.error("getJsPayParams error",e);
			throw new ServiceException(e);
		}
		return jsParams;
	}


	public WxRefundRespone wxRefund(int total_fee, int refund_fee,String out_refund_no,String out_trade_no,String transaction_id,String appid,String mchId,String mchKey,String notifyUrl){
		WxRefundRequest wxRefundRequest = new WxRefundRequest();
		wxRefundRequest.setAppid(appid);
		wxRefundRequest.setMch_id(mchId);
		wxRefundRequest.setNonce_str(WxUtils.createNoncestr());
		wxRefundRequest.setTransaction_id(transaction_id);
		wxRefundRequest.setOut_trade_no(out_trade_no);
		wxRefundRequest.setOut_refund_no(out_refund_no);
		wxRefundRequest.setRefund_fee(refund_fee);
		wxRefundRequest.setTotal_fee(total_fee);
		wxRefundRequest.setNotify_url(notifyUrl);
		String sortStr = WxUtils.createSortStr(WxUtils.bean2map(wxRefundRequest));
		String sign = WxUtils.sign(sortStr, mchKey);
		wxRefundRequest.setSign(sign);
		ClassPathResource cpr = new ClassPathResource("/cer/" + profile +"_apiclient_cert.p12");
		InputStream inputStream = null;
		try {
			inputStream = cpr.getInputStream();
			WxRefundRespone wxRefundRespone = this.p12RestTemplateExecute(wxRefundRequest, inputStream, mchId, mchKey,"https://api.mch.weixin.qq.com/secapi/pay/refund", WxRefundRespone.class,true);
			if (!wxRefundRespone.isSuccess()) {
				throw new ServiceException(wxRefundRespone.getReturn_msg() + StringUtils.trimToEmpty(wxRefundRespone.getErr_code_des()));
			}
			return wxRefundRespone;
		} catch (Exception e){;
			throw  new ServiceException(e);
		} finally {
			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private <T extends  WxTransactionRespone> T p12RestTemplateExecute(Object request, InputStream inputStream, String mchId,String mchKey, String url,Class<T> clazz,boolean checkSign) {
		Assert.notNull(inputStream,"PKCS12 KeyStore is empty");
		Assert.hasLength(mchId,"mchId is empty");
		SSLConnectionSocketFactory sslsf = null;
		try {
			KeyStore ks = null;//eg. PKCS12
			ks = KeyStore.getInstance("PKCS12");
			ks.load(inputStream, mchId.toCharArray());
			// Trust own CA and all self-signed certs
			SSLContext sslcontext = SSLContexts.custom()
					.loadKeyMaterial(ks, mchId.toCharArray())
					.build();
			// Allow TLSv1 protocol only
			sslsf = new SSLConnectionSocketFactory(
					sslcontext,
					new String[]{"TLSv1"},
					null,
					SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);

		} catch (Exception e) {
			throw  new ServiceException(e);
		}
		CloseableHttpClient httpclient = HttpClients.custom()
				.setSSLSocketFactory(sslsf)
				.build();
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpclient);
		clientHttpRequestFactory.setConnectionRequestTimeout(3000);
		clientHttpRequestFactory.setReadTimeout(6000);
		RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
		restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
		String xml = WxUtils.beanToXml(request);
		logger.debug("p12RestTemplate-xml:{}",xml);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MEDIATYPE_APLICATION_XML_UTF8);
		HttpEntity<String> httpEntity = new HttpEntity<>(xml, headers);
		String resultXml = restTemplate.postForObject(url, httpEntity, String.class);
		logger.debug("p12RestTemplate-xmlRespone:{}",resultXml);
		T t = WxUtils.xmlToBean(resultXml, clazz);
		//签名验证
		if (checkSign  && t.isSuccess()) {
			TreeMap<String, Object> xml2map = WxUtils.xml2map(resultXml);
			//验证签名
			String sign = (String) xml2map.remove("sign");
			String createSign = WxUtils.sign(WxUtils.createSortStr(xml2map), mchKey);
			if (!createSign.equals(sign)) {
				logger.error("check refund sign error sign :{} createSign:{}",sign,createSign);
				throw new ServiceException("refund sign error");
			}
		}
		return t;
	}

	public WxTransfersRespone transfers(String mch_appid,String mchId,String mchKey,String partner_trade_no,String openid,int amount,String desc,String ip){
		WxTransfersRequest wxTransfersRequest = new WxTransfersRequest();
		wxTransfersRequest.setMch_appid(mch_appid);
		wxTransfersRequest.setMchid(mchId);
		wxTransfersRequest.setPartner_trade_no(partner_trade_no);
		wxTransfersRequest.setOpenid(openid);
		wxTransfersRequest.setNonce_str(WxUtils.createNoncestr());
		wxTransfersRequest.setAmount(amount);
		wxTransfersRequest.setDesc(desc);
		wxTransfersRequest.setSpbill_create_ip(ip);
		String sortStr = WxUtils.createSortStr(WxUtils.bean2map(wxTransfersRequest));
		String sign = WxUtils.sign(sortStr, mchKey);
		wxTransfersRequest.setSign(sign);
		ClassPathResource cpr = new ClassPathResource("/cer/" + profile +"_apiclient_cert.p12");
		InputStream inputStream = null;
		try {
			inputStream = cpr.getInputStream();
			WxTransfersRespone wxTransfersRespone = this.p12RestTemplateExecute(wxTransfersRequest, inputStream, mchId, mchKey,"https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers", WxTransfersRespone.class,false);
			return wxTransfersRespone;
		} catch (Exception e){;
			throw  new ServiceException(e);
		} finally {
			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		}

	public WxTransfersQryRespone transfersQry(String appid,String mchId,String mchKey,String partner_trade_no){
		WxTransfersQryRequest wxTransfersQryRequest = new WxTransfersQryRequest();
		wxTransfersQryRequest.setAppid(appid);
		wxTransfersQryRequest.setMch_id(mchId);
		wxTransfersQryRequest.setPartner_trade_no(partner_trade_no);
		wxTransfersQryRequest.setNonce_str(WxUtils.createNoncestr());
		String sortStr = WxUtils.createSortStr(WxUtils.bean2map(wxTransfersQryRequest));
		String sign = WxUtils.sign(sortStr, mchKey);
		wxTransfersQryRequest.setSign(sign);
		ClassPathResource cpr = new ClassPathResource("/cer/" + profile +"_apiclient_cert.p12");
		InputStream inputStream = null;
		try {
			inputStream = cpr.getInputStream();
			WxTransfersQryRespone wxTransfersQryRespone = this.p12RestTemplateExecute(wxTransfersQryRequest, inputStream, mchId, mchKey,"https://api.mch.weixin.qq.com/mmpaymkttransfers/gettransferinfo", WxTransfersQryRespone.class,false);
			return wxTransfersQryRespone;
		} catch (Exception e){;
			throw  new ServiceException(e);
		} finally {
			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


}

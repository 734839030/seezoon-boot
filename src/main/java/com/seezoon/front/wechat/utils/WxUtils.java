package com.seezoon.front.wechat.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.seezoon.boot.common.utils.CodecUtils;
import com.seezoon.boot.context.exception.ServiceException;

public class WxUtils {

	/**
	 * 随机串
	 * @return
	 */
	public static String createNoncestr() {
		return RandomStringUtils.randomAlphanumeric(16);
	}
	/**
	 * 时间戳
	 * 
	 * @return
	 */
	public static long createTimestamp() {
		return System.currentTimeMillis() / 1000;
	}

	/**
	 * 生成排序字符串
	 * @param map
	 * @return
	 */
	public static String createSortStr(TreeMap<String, Object> map) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (null!=entry.getValue() && StringUtils.isNotEmpty(entry.getValue().toString())) {
				sb.append(entry.getKey()).append("=").append(entry.getValue())
						.append("&");
			}
		}
		return sb.substring(0, sb.length() - 1);
	}
	public static String beanToXml(Object bean) {
		 Assert.notNull(bean,"bean 为null");
		 XmlMapper xmlMapper = new XmlMapper();
		 xmlMapper.setSerializationInclusion(Include.NON_NULL);
		 try {
			return xmlMapper.writeValueAsString(bean);
		} catch (JsonProcessingException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	public static <T> T xmlToBean(String xml, Class<T> clazz) {
		 Assert.hasLength(xml,"xml 为空");
		 XmlMapper xmlMapper = new XmlMapper();
		 xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			return xmlMapper.readValue(xml, clazz);
		} catch (IOException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	public static TreeMap<String,Object> xml2map(String xml){
		Assert.hasLength(xml,"xml为空");
		XmlMapper xmlMapper = new XmlMapper();
		try {
			TreeMap<String,Object> readValue = xmlMapper.readValue(xml, TreeMap.class);
			return readValue;
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
	}
	public static TreeMap<String,Object> bean2map(Object object) {
		TreeMap<String,Object> treeMap = new TreeMap<>();
		Assert.notNull(object,"object 为null");
		Class<? extends Object> clazz = object.getClass();
		Field[] declaredFields = clazz.getDeclaredFields();
		//也要包括父类
		Field[] declaredFields2 = clazz.getSuperclass().getDeclaredFields();
		Field[] all = ArrayUtils.addAll(declaredFields, declaredFields2);
		for (Field field:all) {
			ReflectionUtils.makeAccessible(field);
			Object value = ReflectionUtils.getField(field, object);
			if (null != value) {
				treeMap.put(field.getName(), value);
			}
		}
		return treeMap;
	}
	/**
	 * 初始化out对象,用于输出相应格式文档到前台
	 */
	public static void outPrint(HttpServletResponse response,String s) {
		PrintWriter out = null;
		try {
			response.setCharacterEncoding("utf-8");
			response.setContentType("application/xml;utf-8");
			out = response.getWriter();
			// 输出到前台
			out.print(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 关闭输出流
		out.close();
	}
	public static String sign(String sortStr,String mchKey) {
		try {
			sortStr += "&key=" + mchKey;
			String md5Upper = CodecUtils.md5(sortStr).toUpperCase();
			return md5Upper;
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		} 
	}
	public static void main(String[] args) {
		System.out.println(createNoncestr());
		System.out.println(createTimestamp());
		System.out.println(new Date().getTime());
	}
}

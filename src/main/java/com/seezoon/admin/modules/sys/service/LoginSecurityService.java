package com.seezoon.admin.modules.sys.service;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import com.seezoon.boot.common.service.BaseService;


@Service
public class LoginSecurityService extends BaseService {
	
	@Resource(name="redisTemplate")
	private ValueOperations<String, Integer> valueOperations;
	private String LOCK_PREFIX = "login_cnt_";

	public void unLock(String loginName) {
		Assert.hasLength(loginName, "loginName 为空");
		valueOperations.getOperations().delete(LOCK_PREFIX + loginName);
	}

	public Integer getLoginFailCount(String loginName) {
		try {
			Integer cnt = valueOperations.get(LOCK_PREFIX + loginName);
			return null == cnt ? 0:cnt;
		} finally {
			valueOperations.getOperations().expire(LOCK_PREFIX + loginName, 24, TimeUnit.HOURS);
		}
	}

	public boolean isLocked(String loginName) {
		return  getLoginFailCount(loginName) >= 5;
	}
	public Long incrementLoginFailTimes(String loginName) {
		try {
			//实际存放在redis 中是integer的，超过integer才会到long spring data increment 
			//在GenericFastJsonRedisSerializer/GenericJackson2JsonRedisSerializer 下的bug
			//测试RedisAtomicLong 也会有类型问题，为了改动小故采用integer 正确使用是通过increment 获取值，而不是直接get
			Long increment = valueOperations.increment(LOCK_PREFIX + loginName, 1);
			return increment;
		} finally {
			valueOperations.getOperations().expire(LOCK_PREFIX + loginName, 24, TimeUnit.HOURS);
		}
	}

	public void clearLoginFailTimes(String loginName) {
		valueOperations.getOperations().delete(LOCK_PREFIX + loginName);
	}
}

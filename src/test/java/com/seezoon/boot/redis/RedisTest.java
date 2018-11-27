package com.seezoon.boot.redis;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.seezoon.boot.BaseApplicationTest;
import com.seezoon.boot.context.dto.AdminUser;

public class RedisTest extends BaseApplicationTest{

	@Autowired
	RedisTemplate<String, String> redisTemplate;
	@Resource(name="redisTemplate")
	ValueOperations<String, AdminUser> valueOperations;
	@Autowired
	private Environment environment;
	@Test
	public void t1() {
		AdminUser u = new AdminUser("122", "322");
		valueOperations.set("kk", u);
		AdminUser adminUser = valueOperations.get("kk");
		System.out.println(adminUser.getDsf());
		redisTemplate.opsForValue().set("a1","1");
		
	}
	@Test
	public void t2() {
		String property = environment.getProperty("spring.session.redis.namespace");
		System.out.println(property);
	}
}

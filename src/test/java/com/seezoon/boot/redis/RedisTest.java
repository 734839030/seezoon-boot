package com.seezoon.boot.redis;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;

import com.seezoon.boot.BaseApplicationTest;

public class RedisTest extends BaseApplicationTest{

	@Autowired
	RedisTemplate<String, String> redisTemplate;
	@Autowired
	private Environment environment;
	@Test
	public void t1() {
		redisTemplate.opsForValue().set("a1","1");
		
	}
	@Test
	public void t2() {
		String property = environment.getProperty("spring.session.redis.namespace");
		System.out.println(property);
	}
}

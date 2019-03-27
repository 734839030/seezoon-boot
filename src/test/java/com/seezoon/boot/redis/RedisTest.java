package com.seezoon.boot.redis;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import com.seezoon.boot.BaseApplicationTest;
import com.seezoon.boot.context.dto.AdminUser;

public class RedisTest extends BaseApplicationTest{

	@Autowired
	RedisTemplate<String, String> redisTemplate;
	@Resource(name="redisTemplate")
	ValueOperations<String, AdminUser> valueOperations;
	@Autowired
	private Environment environment;
	
	@Resource(name="redisTemplate")
	private ValueOperations<String, Long> longValueOperations;
	@Resource(name="redisTemplate")
	private ValueOperations<String, Long> long1ValueOperations;
	
	@Test
	public void  redisAtomicLong() {
		RedisAtomicLong redisAtomicLong = new RedisAtomicLong("cnt", long1ValueOperations.getOperations());
	}
	@Test
	public void longSetTest() {
		long1ValueOperations.set("111", 1l);
		Long long1 = long1ValueOperations.get("111");
		System.out.println(long1);
	}
	@Test
	public void increment() {
		//Long increment = longValueOperations.increment("increment", 1);
		//redisTemplate.setValueSerializer(new StringRedisSerializer());
		Long increment = redisTemplate.opsForValue().increment("increment", 1);
		System.out.println(longValueOperations.get("increment"));
		Long long1 = longValueOperations.get("increment");
		System.out.println(increment);
		System.out.println(long1);
	}
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

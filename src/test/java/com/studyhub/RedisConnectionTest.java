package com.studyhub;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
public class RedisConnectionTest {

	@Autowired
	StringRedisTemplate redisTemplate;

	@Test
	void 연결_확인() {
		redisTemplate.opsForValue().set("test", "hello");
		assertThat(redisTemplate.opsForValue().get("test")).isEqualTo("hello");
	}
}

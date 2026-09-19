package com.studyhub.common.security;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenBlacklist {

	private static final String KEY_PREFIX = "blacklist:";
	private final StringRedisTemplate redisTemplate;

	public void addBlacklist(String jti, long ttlMillis) {
		if (ttlMillis <= 0) {
			return;
		}
		redisTemplate.opsForValue().set(makeKey(jti), "", ttlMillis, TimeUnit.MILLISECONDS);
	}

	public boolean containsBlacklist(String jti) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(makeKey(jti)));
	}

	private String makeKey(String jti) {
		return KEY_PREFIX + jti;
	}
}

package com.studyhub.common.security;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RefreshTokenStore {

	private static final String KEY_PREFIX = "refresh:";
	private final StringRedisTemplate redisTemplate;

	public void save(Long memberId, String tokenHash, long ttlMillis) {
		redisTemplate.opsForValue().set(makeKey(memberId), tokenHash, ttlMillis, TimeUnit.MILLISECONDS);
	}

	public Optional<String> find(Long memberId) {
		return Optional.ofNullable(redisTemplate.opsForValue().get(makeKey(memberId)));
	}

	public void delete(Long memberId) {
		redisTemplate.delete(makeKey(memberId));
	}

	private String makeKey(Long memberId) {
		return KEY_PREFIX + memberId;
	}
}

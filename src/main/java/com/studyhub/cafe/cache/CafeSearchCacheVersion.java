package com.studyhub.cafe.cache;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CafeSearchCacheVersion {

	private static final String VERSION_KEY = "cafe:search:version";
	private final StringRedisTemplate redisTemplate;

	public long getVersion() {
		String version = redisTemplate.opsForValue().get(VERSION_KEY);
		return version == null ? 0L : Long.parseLong(version);
	}

	public void increaseAfterCommit() {
		if (!TransactionSynchronizationManager.isSynchronizationActive()) {
			increase();
			return;
		}
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				try {
					increase();
				} catch (Exception e) {
					log.warn("검색 캐시 버전 증가 실패", e);
				}
			}
		});
	}

	private void increase() {
		redisTemplate.opsForValue().increment(VERSION_KEY);
	}
}

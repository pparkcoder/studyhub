package com.studyhub.ai.search.cache;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.studyhub.ai.search.port.AiSearchCondition;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiSearchConditionCache {

	private static final String KEY_PREFIX = "ai:search:condition";
	private final StringRedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;

	public void put(String key, AiSearchCondition condition) {
		try {
			String json = objectMapper.writeValueAsString(condition);
			redisTemplate.opsForValue().set(key, json, Duration.ofDays(1));
		} catch (Exception e) {
			log.warn("캐시 저장 실패. key ={}", key, e);
		}
	}

	public Optional<AiSearchCondition> get(String key) {
		try {
			String json = redisTemplate.opsForValue().get(key);
			if (json == null)
				return Optional.empty();
			return Optional.of(objectMapper.readValue(json, AiSearchCondition.class));
		} catch (Exception e) {
			log.warn("캐시 조회 실패. key = {}", key, e);
			return Optional.empty();
		}
	}

	public String makeKey(String query, List<String> regions) {
		String regionHash = Integer.toHexString(String.join(",", regions).hashCode());
		return KEY_PREFIX + ":" + regionHash + ":" + normalizeQuery(query);
	}

	private String normalizeQuery(String query) {
		return query.trim().replaceAll("\\s+", " ").toLowerCase();
	}
}

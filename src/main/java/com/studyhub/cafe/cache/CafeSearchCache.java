package com.studyhub.cafe.cache;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.studyhub.cafe.dto.response.CafeSearchResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class CafeSearchCache {

	private static final String KEY_PREFIX = "cafe:search:v";
	private final CafeSearchCacheVersion cacheVersion;
	private final StringRedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;

	public void put(String key, List<CafeSearchResponse> response) {
		try {
			String json = objectMapper.writeValueAsString(response);
			redisTemplate.opsForValue().set(key, json, Duration.ofMinutes(10));
		} catch (Exception e) {
			log.warn("검색 캐시 저장 실패. key = {}", key, e);
		}
	}

	public Optional<List<CafeSearchResponse>> get(String key) {
		try {
			String json = redisTemplate.opsForValue().get(key);
			if (json == null) {
				return Optional.empty();
			}
			return Optional.of(objectMapper.readValue(json, new TypeReference<List<CafeSearchResponse>>() {
			}));
		} catch (Exception e) {
			log.warn("검색 캐시 조회 실패. key = {}", key, e);
			return Optional.empty();
		}
	}

	public Optional<String> makeKey(String region, String keyword) {
		try {
			long version = cacheVersion.getVersion();
			return Optional.of(KEY_PREFIX + version + ":" + normalize(region) + ":" + normalize(keyword));
		} catch (Exception e) {
			log.warn("검색 캐시 버전 조회 실패", e);
			return Optional.empty();
		}
	}

	private String normalize(String value) {
		if (!StringUtils.hasText(value)) {
			return "_";
		}
		return value.trim().toLowerCase();
	}
}

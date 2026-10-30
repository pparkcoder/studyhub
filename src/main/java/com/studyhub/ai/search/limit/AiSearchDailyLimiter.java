package com.studyhub.ai.search.limit;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiSearchDailyLimiter {

	private static final String KEY_PREFIX = "ai:search:count";
	private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
	private final StringRedisTemplate redisTemplate;

	@Value("${ai.search.daily-limit}")
	private int dayLimit;

	public boolean isExceeded(Long memberId) {
		LocalDate today = LocalDate.now(SEOUL);
		String key = makeKey(memberId, today);
		try {
			Long count = redisTemplate.opsForValue().increment(key);
			if (count != null && count == 1) {
				ZonedDateTime nextDay = today.plusDays(1).atStartOfDay(SEOUL);
				redisTemplate.expireAt(key, nextDay.toInstant());
			}
			return count != null && count > dayLimit;
		} catch (Exception e) {
			log.warn("AI 검색 호출 횟수 확인 실패. 제한 없이 허용합니다. memberId={}", memberId, e);
			return false;
		}
	}

	private String makeKey(Long memberId, LocalDate date) {
		return KEY_PREFIX + ":" + memberId + ":" + date.format(DATE_FORMATTER);
	}

}

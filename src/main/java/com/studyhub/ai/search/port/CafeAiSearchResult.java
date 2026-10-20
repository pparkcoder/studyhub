package com.studyhub.ai.search.port;

import java.time.LocalTime;

public record CafeAiSearchResult(
	Long id,
	String name,
	String address,
	String region,
	String mainImageUrl,
	int seatCount,
	LocalTime openTime,
	LocalTime closeTime
) {
}

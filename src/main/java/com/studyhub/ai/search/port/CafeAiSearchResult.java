package com.studyhub.ai.search.port;

import java.time.LocalTime;

public record CafeSearchResult(
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

package com.studyhub.ai.search.port;

public record AiSearchCondition(
	String requestedRegion,
	String region,
	String keyword,
	Boolean openNow
) {
}

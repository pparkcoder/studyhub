package com.studyhub.ai.search.port;

public record AiSearchCondition(
	String region,
	String keyword,
	Boolean openNow
) {
}

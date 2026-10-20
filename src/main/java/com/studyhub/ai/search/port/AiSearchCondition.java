package com.studyhub.ai.search.port;

public record SearchCondition(
	String region,
	String keyword,
	Boolean openNow
) {
}

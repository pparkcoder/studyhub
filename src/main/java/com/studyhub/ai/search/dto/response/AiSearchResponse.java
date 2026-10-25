package com.studyhub.ai.search.dto.response;

import java.util.List;

import com.studyhub.ai.search.port.AiSearchCondition;
import com.studyhub.ai.search.port.CafeAiSearchResult;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AiSearchResponse {

	private AiSearchCondition condition;
	private List<CafeAiSearchResult> cafes;
	private boolean aiUsed;

	private AiSearchResponse(AiSearchCondition condition,
		List<CafeAiSearchResult> cafeAiSearchResult, boolean aiUsed) {
		this.condition = condition;
		this.cafes = cafeAiSearchResult;
		this.aiUsed = aiUsed;
	}

	public static AiSearchResponse of(AiSearchCondition condition,
		List<CafeAiSearchResult> cafeAiSearchResult, boolean aiUsed) {
		return new AiSearchResponse(
			condition,
			cafeAiSearchResult,
			aiUsed
		);
	}

}

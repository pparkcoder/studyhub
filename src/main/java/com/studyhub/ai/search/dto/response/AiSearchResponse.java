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

	private AiSearchResponse(AiSearchCondition condition,
		List<CafeAiSearchResult> cafeAiSearchResult) {
		this.condition = condition;
		this.cafes = cafeAiSearchResult;
	}

	public static AiSearchResponse of(AiSearchCondition condition,
		List<CafeAiSearchResult> cafeAiSearchResult) {
		return new AiSearchResponse(
			condition,
			cafeAiSearchResult
		);
	}

}

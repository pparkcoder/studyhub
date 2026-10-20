package com.studyhub.cafe.adapter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.studyhub.ai.search.port.AiSearchCondition;
import com.studyhub.ai.search.port.CafeAiSearchPort;
import com.studyhub.ai.search.port.CafeAiSearchResult;
import com.studyhub.cafe.dto.request.CafeSearchRequest;
import com.studyhub.cafe.dto.response.CafeSearchResponse;
import com.studyhub.cafe.service.CafeService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CafeAiSearchPortImpl implements CafeAiSearchPort {

	private final CafeService cafeService;

	@Override
	public List<CafeAiSearchResult> search(AiSearchCondition condition) {
		CafeSearchRequest cafeSearchRequest = CafeSearchRequest.of(condition.region(), condition.keyword(),
			condition.openNow());
		return cafeService.search(cafeSearchRequest)
			.stream()
			.map(this::toResult)
			.toList();
	}

	private CafeAiSearchResult toResult(CafeSearchResponse response) {
		return new CafeAiSearchResult(
			response.getId(),
			response.getName(),
			response.getAddress(),
			response.getRegion(),
			response.getMainImageUrl(),
			response.getSeatCount(),
			response.getOpenTime(),
			response.getCloseTime()
		);
	}
}

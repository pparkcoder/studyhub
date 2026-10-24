package com.studyhub.ai.search.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.studyhub.ai.search.dto.request.AiSearchRequest;
import com.studyhub.ai.search.dto.response.AiSearchResponse;
import com.studyhub.ai.search.port.AiSearchCondition;
import com.studyhub.ai.search.port.AiSearchConditionExtractor;
import com.studyhub.ai.search.port.CafeAiSearchPort;
import com.studyhub.ai.search.port.CafeAiSearchResult;
import com.studyhub.ai.search.port.CafeRegionPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiSearchService {

	private final CafeRegionPort cafeRegionPort;
	private final CafeAiSearchPort cafeAiSearchPort;
	private final AiSearchConditionExtractor aiSearchConditionExtractor;

	public AiSearchResponse search(AiSearchRequest request) {
		List<String> activeRegions = cafeRegionPort.findActiveRegions();
		AiSearchCondition extracted = aiSearchConditionExtractor.extract(request.getQuery(), activeRegions);
		AiSearchCondition condition = validate(extracted, activeRegions);

		if (condition.requestedRegion() != null && condition.region() == null) {
			return AiSearchResponse.of(condition, List.of());
		}
		List<CafeAiSearchResult> response = cafeAiSearchPort.search(condition);
		return AiSearchResponse.of(condition, response);
	}

	private AiSearchCondition validate(AiSearchCondition extract, List<String> regions) {
		if (extract == null) {
			return new AiSearchCondition(null, null, null, null);
		}
		return new AiSearchCondition(
			validateRequestedRegion(extract.requestedRegion()),
			validateRegion(extract.region(), regions),
			validateKeyword(extract.keyword()),
			Boolean.TRUE.equals(extract.openNow()) ? Boolean.TRUE : null
		);
	}

	private String validateRequestedRegion(String requestedRegion) {
		if (requestedRegion == null) {
			return null;
		}
		String trimmed = requestedRegion.trim();
		if (trimmed.isEmpty() || trimmed.length() > 30) {
			return null;
		}
		return trimmed;
	}

	private String validateRegion(String region, List<String> regions) {
		if (region == null) {
			return null;
		}
		return regions.contains(region) ? region : null;
	}

	private String validateKeyword(String keyword) {
		if (keyword == null) {
			return null;
		}
		String trimmed = keyword.trim();
		if (trimmed.isEmpty() || trimmed.length() > 50) {
			return null;
		}
		return trimmed;
	}
}

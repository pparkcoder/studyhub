package com.studyhub.ai.search.service;

import java.util.List;

import org.springframework.stereotype.Service;

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

	public List<CafeAiSearchResult> search(String query) {
		List<String> activeRegions = cafeRegionPort.findActiveRegions();
		AiSearchCondition extracted = aiSearchConditionExtractor.extract(query, activeRegions);
		AiSearchCondition validated = validate(extracted, activeRegions);
		return cafeAiSearchPort.search(validated);
	}

	private AiSearchCondition validate(AiSearchCondition extract, List<String> regions) {
		if (extract == null) {
			return new AiSearchCondition(null, null, null);
		}
		return new AiSearchCondition(
			validateRegion(extract.region(), regions),
			validateKeyword(extract.keyword()),
			Boolean.TRUE.equals(extract.openNow()) ? Boolean.TRUE : null
		);
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

package com.studyhub.ai.search.service;

import java.util.List;
import java.util.Optional;

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
	private static final List<String> OPEN_NOW_WORDS = List.of("지금", "영업 중", "영업중", "열려", "열린", "운영 중", " 운영중");

	public AiSearchResponse search(AiSearchRequest request) {
		String query = request.getQuery();
		List<String> activeRegions = cafeRegionPort.findActiveRegions();
		Optional<AiSearchCondition> extracted = aiSearchConditionExtractor.extract(query, activeRegions);
		boolean aiUsed = extracted.isPresent();
		AiSearchCondition aiSearchCondition = extracted.orElseGet(() -> extractByRule(query, activeRegions));
		AiSearchCondition condition = validate(aiSearchCondition, activeRegions);

		if (condition.requestedRegion() != null && condition.region() == null) {
			return AiSearchResponse.of(condition, List.of(), aiUsed);
		}
		List<CafeAiSearchResult> response = cafeAiSearchPort.search(condition);
		return AiSearchResponse.of(condition, response, aiUsed);
	}

	// AI 추출 실패 시 규칙 추출
	private AiSearchCondition extractByRule(String query, List<String> regions) {
		return new AiSearchCondition(
			null,
			findRegionByRule(query, regions),
			null,
			containsAny(query) ? Boolean.TRUE : null
		);
	}

	private String findRegionByRule(String query, List<String> regions) {
		for (String region : regions) {
			String lastWord = region.substring(region.lastIndexOf(' ') + 1);
			if (query.contains(normalizeRegion(lastWord))) {
				return region;
			}
		}
		return null;
	}

	private String normalizeRegion(String region) {
		String lastWord = region.substring(region.lastIndexOf(' ') + 1);
		String baseName = lastWord.replaceAll("(구|시|군|동)$", "");
		return baseName.length() >= 2 ? baseName : lastWord;
	}

	private boolean containsAny(String query) {
		return OPEN_NOW_WORDS.stream().anyMatch(query::contains);
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

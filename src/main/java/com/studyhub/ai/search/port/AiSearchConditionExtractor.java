package com.studyhub.ai.search.port;

import java.util.List;

public interface AiSearchConditionExtractor {
	AiSearchCondition extract(String query, List<String> regions);
}

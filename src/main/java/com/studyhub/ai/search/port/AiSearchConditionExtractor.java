package com.studyhub.ai.search.port;

import java.util.List;
import java.util.Optional;

public interface AiSearchConditionExtractor {
	Optional<AiSearchCondition> extract(String query, List<String> regions);
}

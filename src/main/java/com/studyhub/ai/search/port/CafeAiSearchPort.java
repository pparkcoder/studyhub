package com.studyhub.ai.search.port;

import java.util.List;

public interface CafeAiSearchPort {

	List<CafeAiSearchResult> search(AiSearchCondition condition);
}

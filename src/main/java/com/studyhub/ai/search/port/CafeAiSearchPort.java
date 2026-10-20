package com.studyhub.ai.search.port;

import java.util.List;

public interface CafeSearchPort {

	List<CafeAiSearchResult> search(AiSearchCondition condition);
}

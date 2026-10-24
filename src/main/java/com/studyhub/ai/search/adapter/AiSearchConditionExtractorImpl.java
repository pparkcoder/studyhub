package com.studyhub.ai.search.adapter;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import com.studyhub.ai.search.port.AiSearchCondition;
import com.studyhub.ai.search.port.AiSearchConditionExtractor;

@Component
public class AiSearchConditionExtractorImpl implements AiSearchConditionExtractor {

	private static final String SYSTEM_PROMPT = """
		너는 스터디카페 검색 문장을 검색 조건으로 바꾸는 역할이다.		
		규칙:
		1. region: 사용자가 지역을 말한 경우에만, 다음 목록 중 하나를 글자 그대로 고른다. 목록: {regions}
		   목록에 없는 지역이거나 지역을 말하지 않았으면 null로 둔다.
		2. keyword: 사용자가 특정 카페 이름을 말한 경우에만 그 이름을 넣는다.
		   "카페", "스터디카페", "독서실", "공부할 곳" 같은 일반적인 단어는 넣지 않고 null로 둔다.
		3. openNow: 지금 열려 있는 곳, 영업 중인 곳을 원할 때만 true로 둔다. 그 외에는 null로 둔다.
		4. 위 세 가지로 표현할 수 없는 조건(분위기, 시설, 특정 시각 등)은 무시한다.
		""";

	private final ChatClient chatClient;

	public AiSearchConditionExtractorImpl(ChatClient.Builder chatClientBuilder) {
		this.chatClient = chatClientBuilder.build();
	}

	@Override
	public AiSearchCondition extract(String query, List<String> regions) {
		return chatClient.prompt()
			.system(system -> system
				.text(SYSTEM_PROMPT)
				.param("regions", String.join(", ", regions)))
			.user(query)
			.call()
			.entity(AiSearchCondition.class);
	}
}

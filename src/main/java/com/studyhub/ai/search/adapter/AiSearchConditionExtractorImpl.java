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
		1. requestedRegion : 사용자가 지역을 말한 경우, 사용자가 말한 지역 표현을 그대로 넣는다.
		   지역을 말하지 않았으면 null로 둔다. "근처", "주변"처럼 특정 지역이 아닌 표현은 지역으로 보지 않는다.
		2. region: requestedRegion이 다음 목록의 지역 중 하나에 해당하면, 그 목록의 값을 글자 그대로 넣는다.
		   해당하는 지역이 없으면 null로 둔다. 목록: {regions}
		3. keyword: 사용자가 특정 카페 이름을 말한 경우에만 그 이름을 넣는다.
		   "카페", "스터디카페", "독서실", "공부할 곳" 같은 일반적인 단어는 넣지 않고 null로 둔다.
		4. openNow: 지금 열려 있는 곳, 영업 중인 곳을 원할 때만 true로 둔다. 그 외에는 null로 둔다.
		5. 위 규칙으로 표현할 수 없는 조건(분위기, 시설, 특정 시각 등)은 무시한다.
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

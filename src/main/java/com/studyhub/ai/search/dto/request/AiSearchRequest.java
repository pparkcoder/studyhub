package com.studyhub.ai.search.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class AiSearchRequest {

	@NotBlank(message = "검색하실 문장을 입력해주세요.")
	@Size(max = 200)
	private String query;
}

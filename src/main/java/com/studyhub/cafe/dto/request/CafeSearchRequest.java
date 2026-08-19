package com.studyhub.cafe.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CafeSearchRequest {

	private String region;
	private String keyword;
	private Boolean openNow; // 영업 여부
}

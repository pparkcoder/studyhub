package com.studyhub.cafe.dto.request;

import lombok.Getter;

@Getter
public class CafeSearchRequest {

	private String region;
	private String keyword;
	private Boolean openNow; // 영업 여부
}

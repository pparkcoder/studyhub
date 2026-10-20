package com.studyhub.cafe.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CafeSearchRequest {

	private String region;
	private String keyword;
	private Boolean openNow; // 영업 여부

	public static CafeSearchRequest of(String region, String keyword, Boolean openNow) {
		return new CafeSearchRequest(region, keyword, openNow);
	}
}

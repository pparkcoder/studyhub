package com.studyhub.cafe.adapter.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoAddress(
	@JsonProperty("region_1depth_name") String region1depthName,
	@JsonProperty("region_2depth_name") String region2depthName,
	@JsonProperty("b_code") String bCode
) {
}

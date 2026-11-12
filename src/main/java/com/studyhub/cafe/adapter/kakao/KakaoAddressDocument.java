package com.studyhub.cafe.adapter.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoAddressDocument(
	@JsonProperty("address_type") String addressType,
	String x,
	String y,
	KakaoAddress address
) {
}

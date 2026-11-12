package com.studyhub.cafe.adapter.kakao;

import java.util.List;

public record KakaoAddressResponse(
	List<KakaoAddressDocument> documents
) {
}

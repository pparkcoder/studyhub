package com.studyhub.cafe.dto.response;

import com.studyhub.cafe.domain.Cafe;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CafeRegisterResponse {

	private final Long cafeId;

	public static CafeRegisterResponse from(Cafe cafe) {
		return new CafeRegisterResponse(cafe.getId());
	}
}

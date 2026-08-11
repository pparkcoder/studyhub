package com.studyhub.member.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
	MEMBER("일반사용자"),
	OWNER("업체관계자");

	private final String description;
}

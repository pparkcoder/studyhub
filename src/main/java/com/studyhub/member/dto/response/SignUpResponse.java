package com.studyhub.member.dto.response;

import com.studyhub.member.domain.Member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SignUpResponse {

	private final Long memberId;

	public static SignUpResponse from(Member member) {
		return new SignUpResponse(member.getId());
	}

}

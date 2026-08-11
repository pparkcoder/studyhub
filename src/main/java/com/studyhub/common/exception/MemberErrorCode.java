package com.studyhub.common.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MemberErrorCode implements ErrorCode {

	DUPLICATE_USERNAME(HttpStatus.CONFLICT, "MEMBER_001", "이미 사용 중인 아이디입니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;

	@Override
	public HttpStatus getStatus() {
		return status;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getMessage() {
		return message;
	}
}

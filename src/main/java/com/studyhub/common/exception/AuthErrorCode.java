package com.studyhub.common.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {

	DUPLICATE_USERNAME(HttpStatus.CONFLICT, "AUTH_001", "이미 사용 중인 아이디입니다."),
	LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH_002", "아이디 또는 비밀번호가 일치하지 않습니다");

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

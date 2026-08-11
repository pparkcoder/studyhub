package com.studyhub.common.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {

	LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH_001", "아이디 또는 비밀번호가 일치하지 않습니다");

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

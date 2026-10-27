package com.studyhub.common.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CommonErrorCode implements ErrorCode {

	AI_SEARCH_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "COMMON_001", "AI 호출 횟수를 초과하였습니다.(하루 최대 호출 횟수 : 20)");

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

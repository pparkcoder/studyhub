package com.studyhub.common.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CafeErrorCode implements ErrorCode {

	OWNER_NOT_FOUND(HttpStatus.NOT_FOUND, "CAFE_001", "존재하지 않는 회원입니다."),
	OWNER_WITHDRAWN(HttpStatus.NOT_FOUND, "CAFE_002", "탈퇴한 회원입니다."),
	NOT_OWNER_ROLE(HttpStatus.UNAUTHORIZED, "CAFE_003", "카페를 등록할 권한이 없습니다."),
	SEAT_NOT_FOUND(HttpStatus.NOT_FOUND, "CAFE_004", "존재하지 않는 좌석입니다.");

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

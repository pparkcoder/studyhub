package com.studyhub.common.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MemberErrorCode implements ErrorCode {

	DUPLICATE_USERNAME(HttpStatus.CONFLICT, "MEMBER_001", "이미 사용 중인 아이디입니다."),
	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_002", "존재하지 않는 회원입니다."),
	ACTIVE_RESERVATION_EXISTS(HttpStatus.CONFLICT, "MEMBER_003", "진행 중인 예약이 있어 탈퇴할 수 없습니다. 예약을 먼저 취소해주세요."),
	ALREADY_WITHDRAWN(HttpStatus.CONFLICT, "MEMBER_004", "이미 탈퇴한 회원입니다."),
	PASSWORD_MISMATCH(HttpStatus.UNAUTHORIZED, "MEMBER_005", "비밀번호가 일치하지 않습니다.");

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

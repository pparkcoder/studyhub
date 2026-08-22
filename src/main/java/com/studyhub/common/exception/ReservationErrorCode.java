package com.studyhub.common.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ReservationErrorCode implements ErrorCode {

	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "RESERVATION_001", "존재하지 않는 회원입니다."),
	WITHDRAWN_MEMBER(HttpStatus.FORBIDDEN, "RESERVATION_002", "탈퇴한 회원입니다."),
	SEAT_NOT_FOUND(HttpStatus.NOT_FOUND, "RESERVATION_003", "존재하지 않는 좌석입니다."),
	SEAT_DISABLED(HttpStatus.CONFLICT, "RESERVATION_004", "사용할 수 없는 좌석입니다."),
	SEAT_CAFE_MISMATCH(HttpStatus.BAD_REQUEST, "RESERVATION_005", "해당 카페의 좌석이 아닙니다."),
	ALREADY_RESERVED(HttpStatus.CONFLICT, "RESERVATION_006", "이미 예약된 시간대입니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public HttpStatus getStatus() {
		return httpStatus;
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

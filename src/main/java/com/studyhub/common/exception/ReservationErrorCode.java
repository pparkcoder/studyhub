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
	INVALID_START_TIME(HttpStatus.BAD_REQUEST, "RESERVATION_006", "잘못된 시작 시간입니다."),
	ALREADY_RESERVED(HttpStatus.CONFLICT, "RESERVATION_007", "이미 예약된 시간대입니다."),
	RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "RESERVATION_008", "존재하지 않는 예약입니다."),
	NOT_RESERVATION_OWNER(HttpStatus.FORBIDDEN, "RESERVATION_009", "본인의 예약만 취소할 수 있습니다."),
	ALREADY_CANCELLED(HttpStatus.CONFLICT, "RESERVATION_010", "이미 취소된 예약입니다."),
	ALREADY_ENDED(HttpStatus.CONFLICT, "RESERVATION_011", "이미 종료된 좌석입니다.");

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

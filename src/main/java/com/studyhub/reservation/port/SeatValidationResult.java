package com.studyhub.reservation.port;

public enum SeatValidationResult {
	VALID,
	NOT_FOUND,
	DISABLED,
	CAFE_MISMATCH; // seatId가 해당 cafeId 소속이 아닌 경우
}

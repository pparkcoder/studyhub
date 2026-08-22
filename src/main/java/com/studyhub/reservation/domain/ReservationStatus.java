package com.studyhub.reservation.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationStatus {
	RESERVED("예악완료"),
	CANCELLED("예약취소");

	private final String description;
}

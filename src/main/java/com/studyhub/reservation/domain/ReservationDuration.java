package com.studyhub.reservation.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationDuration {
	ONE_HOUR(1, "1시간권"),
	TWO_HOURS(2, "2시간권"),
	FOUR_HOURS(4, "4시간권"),
	SIX_HOURS(6, "6시간권"),
	EIGHT_HOURS(8, "8시간권"),
	TWELVE_HOURS(12, "12시간권");
	
	private final int hours;
	private final String description;
}

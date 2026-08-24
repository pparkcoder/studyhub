package com.studyhub.reservation.port;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CafeInfo {

	private final String cafeName;
	private final String seatNumber;

	public static CafeInfo of(String cafeName, String seatNumber) {
		return new CafeInfo(cafeName, seatNumber);
	}
}

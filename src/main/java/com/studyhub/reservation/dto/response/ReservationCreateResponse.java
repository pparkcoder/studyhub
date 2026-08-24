package com.studyhub.reservation.dto.response;

import com.studyhub.reservation.domain.Reservation;

import lombok.Getter;

@Getter
public class ReservationCreateResponse {

	private final Long reservationId;

	private ReservationCreateResponse(Long reservationId) {
		this.reservationId = reservationId;
	}

	public static ReservationCreateResponse from(Reservation reservation) {
		return new ReservationCreateResponse(reservation.getId());
	}

}

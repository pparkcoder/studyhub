package com.studyhub.cafe.dto.response;

import com.studyhub.cafe.domain.Seat;
import com.studyhub.cafe.domain.SeatStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SeatResponse {

	private Long id;
	private String seatNumber;
	private SeatStatus status;

	private SeatResponse(Long id, String seatNumber, SeatStatus status) {
		this.id = id;
		this.seatNumber = seatNumber;
		this.status = status;
	}

	public static SeatResponse from(Seat seat) {
		return new SeatResponse(
			seat.getId(),
			seat.getSeatNumber(),
			seat.getStatus()
		);
	}
}

package com.studyhub.reservation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ReservationMoveRequest {

	@NotNull(message = "좌석 ID는 필수입니다.")
	private Long seatId;

	@NotNull(message = "좌석 번호는 필수입니다.")
	@Size(max = 10)
	private String seatNumber;
}

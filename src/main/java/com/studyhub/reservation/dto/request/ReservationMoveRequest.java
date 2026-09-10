package com.studyhub.reservation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ReservationMoveRequest {

	@NotNull(message = "좌석 ID는 필수입니다.")
	private Long seatId;
}

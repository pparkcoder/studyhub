package com.studyhub.reservation.dto;

import java.time.LocalDateTime;

import com.studyhub.reservation.domain.ReservationDuration;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ReservationCreateRequest {

	@NotNull(message = "카페 ID는 필수입니다.")
	private Long cafeId;

	@NotNull(message = "좌석 ID는 필수입니다.")
	private Long seatId;

	@NotNull(message = "예약 시작 시간은 필수입니다.")
	private LocalDateTime startTime;

	@NotNull(message = "이용 시간은 필수입니다.")
	private ReservationDuration duration;
}

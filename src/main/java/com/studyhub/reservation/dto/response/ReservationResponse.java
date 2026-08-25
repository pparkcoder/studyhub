package com.studyhub.reservation.dto.response;

import java.time.LocalDateTime;

import com.studyhub.reservation.domain.Reservation;
import com.studyhub.reservation.port.CafeInfo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReservationResponse {

	private Long reservationId;
	private String cafeName;
	private String seatNumber;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private String status;

	private ReservationResponse(Long reservationId, String cafeName, String seatNumber, LocalDateTime startTime,
		LocalDateTime endTime, String status) {
		this.reservationId = reservationId;
		this.cafeName = cafeName;
		this.seatNumber = seatNumber;
		this.startTime = startTime;
		this.endTime = endTime;
		this.status = status;
	}

	public static ReservationResponse of(Reservation reservation, CafeInfo cafeInfo, LocalDateTime now) {
		return new ReservationResponse(
			reservation.getId(),
			cafeInfo.getCafeName(),
			cafeInfo.getSeatNumber(),
			reservation.getStartTime(),
			reservation.getEndTime(),
			resolveStatus(reservation, now)
		);
	}

	private static String resolveStatus(Reservation reservation, LocalDateTime now) {
		if (reservation.isCancelled()) {
			return "취소됨";
		}
		if (reservation.isEnded(now)) {
			return "이용완료";
		}
		if (reservation.getStartTime().isAfter(now)) {
			return "예약됨";
		}
		return "이용중";
	}
}

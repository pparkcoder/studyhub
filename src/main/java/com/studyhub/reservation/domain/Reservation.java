package com.studyhub.reservation.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reservation")
public class Reservation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long memberId;
	private Long cafeId;
	private Long seatId;

	private LocalDateTime startTime;
	private LocalDateTime endTime;

	@Enumerated(EnumType.STRING)
	private ReservationDuration duration;

	@Enumerated(EnumType.STRING)
	private ReservationStatus status;

	@Builder(access = AccessLevel.PRIVATE)
	private Reservation(Long memberId, Long cafeId, Long seatId, LocalDateTime startTime, LocalDateTime endTime,
		ReservationDuration duration, ReservationStatus status) {
		this.memberId = memberId;
		this.cafeId = cafeId;
		this.seatId = seatId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.duration = duration;
		this.status = status;
	}

	public static Reservation reserve(Long memberId, Long cafeId, Long seatId, LocalDateTime startTime,
		ReservationDuration duration) {
		return Reservation.builder()
			.memberId(memberId)
			.cafeId(cafeId)
			.seatId(seatId)
			.startTime(startTime)
			.endTime(startTime.plusHours(duration.getHours()))
			.status(ReservationStatus.RESERVED)
			.build();
	}

	public void cancelReservation() {
		this.status = ReservationStatus.CANCELLED;
	}

}

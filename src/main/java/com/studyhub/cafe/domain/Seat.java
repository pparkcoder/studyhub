package com.studyhub.cafe.domain;

import com.studyhub.common.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "seat")
public class Seat extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "seat_number", nullable = false, length = 10)
	private String seatNumber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cafe_id")
	private Cafe cafe;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private SeatStatus status;

	@Builder(access = AccessLevel.PRIVATE)
	private Seat(String seatNumber, SeatStatus status) {
		this.seatNumber = seatNumber;
		this.status = status;
	}

	public static Seat of(String seatNumber) {
		return Seat.builder()
			.seatNumber(seatNumber)
			.status(SeatStatus.AVAILABLE)
			.build();
	}

	void assignCafe(Cafe cafe) {
		this.cafe = cafe;
	}

	public void disable() {
		this.status = SeatStatus.DISABLED;
	}

	public void enable() {
		this.status = SeatStatus.AVAILABLE;
	}

}

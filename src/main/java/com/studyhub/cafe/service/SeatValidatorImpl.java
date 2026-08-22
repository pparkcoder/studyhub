package com.studyhub.cafe.service;

import org.springframework.stereotype.Component;

import com.studyhub.cafe.domain.Seat;
import com.studyhub.cafe.domain.SeatStatus;
import com.studyhub.cafe.repository.SeatRepository;
import com.studyhub.reservation.port.SeatValidationResult;
import com.studyhub.reservation.port.SeatValidator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SeatValidatorImpl implements SeatValidator {

	private final SeatRepository seatRepository;

	@Override
	public SeatValidationResult validate(Long cafeId, Long seatId) {
		Seat seat = seatRepository.findById(seatId).orElse(null);

		if (seat == null) {
			return SeatValidationResult.NOT_FOUND;
		}
		if (!seat.getCafe().getId().equals(cafeId)) {
			return SeatValidationResult.CAFE_MISMATCH;
		}
		if (seat.getStatus() == SeatStatus.DISABLED) {
			return SeatValidationResult.DISABLED;
		}

		return SeatValidationResult.VALID;
	}
}

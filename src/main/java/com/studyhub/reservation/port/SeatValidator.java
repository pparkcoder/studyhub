package com.studyhub.reservation.port;

public interface SeatValidator {

	SeatValidationResult validate(Long cafeId, Long seatId);
}

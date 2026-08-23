package com.studyhub.reservation.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studyhub.common.exception.BusinessException;
import com.studyhub.common.exception.ReservationErrorCode;
import com.studyhub.reservation.domain.Reservation;
import com.studyhub.reservation.domain.ReservationDuration;
import com.studyhub.reservation.port.MemberValidator;
import com.studyhub.reservation.port.SeatLockPort;
import com.studyhub.reservation.port.SeatValidator;
import com.studyhub.reservation.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {

	private final MemberValidator memberValidator;
	private final SeatValidator seatValidator;
	private final SeatLockPort seatLockPort;
	private final ReservationRepository reservationRepository;

	@Transactional
	public Long reserve(Long memberId, Long cafeId, Long seatId, LocalDateTime startTime,
		ReservationDuration duration) {
		validateMember(memberId);
		validateSeat(cafeId, seatId);

		if (startTime.isBefore(LocalDateTime.now())) {
			throw new BusinessException(ReservationErrorCode.INVALID_START_TIME);
		}
		
		seatLockPort.lock(seatId);
		LocalDateTime endTime = duration.calculateEndTime(startTime);
		boolean existsOverlapping = reservationRepository.existsOverlapping(seatId, startTime, endTime);
		if (existsOverlapping) {
			throw new BusinessException(ReservationErrorCode.ALREADY_RESERVED);
		}

		Reservation reserve = Reservation.reserve(memberId, cafeId, seatId, startTime, duration);
		return reservationRepository.save(reserve).getId();
	}

	private void validateMember(Long memberId) {
		ReservationErrorCode errorCode = switch (memberValidator.validate(memberId)) {
			case NOT_FOUND -> ReservationErrorCode.MEMBER_NOT_FOUND;
			case WITHDRAWN -> ReservationErrorCode.WITHDRAWN_MEMBER;
			case VALID -> null;
		};
		if (errorCode != null) {
			throw new BusinessException(errorCode);
		}
	}

	private void validateSeat(Long cafeId, Long seatId) {
		ReservationErrorCode errorCode = switch (seatValidator.validate(cafeId, seatId)) {
			case NOT_FOUND -> ReservationErrorCode.SEAT_NOT_FOUND;
			case CAFE_MISMATCH -> ReservationErrorCode.SEAT_CAFE_MISMATCH;
			case DISABLED -> ReservationErrorCode.SEAT_DISABLED;
			case VALID -> null;
		};
		if (errorCode != null) {
			throw new BusinessException(errorCode);
		}
	}
}

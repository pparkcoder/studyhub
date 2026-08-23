package com.studyhub.cafe.service;

import org.springframework.stereotype.Component;

import com.studyhub.cafe.repository.SeatRepository;
import com.studyhub.common.exception.BusinessException;
import com.studyhub.common.exception.CafeErrorCode;
import com.studyhub.reservation.port.SeatLockPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SeacLockPortImpl implements SeatLockPort {

	private final SeatRepository seatRepository;

	@Override
	public void lock(long seatId) {
		seatRepository.findByIdWithPessimisticLock(seatId)
			.orElseThrow(() -> new BusinessException(CafeErrorCode.SEAT_NOT_FOUND));
	}
}

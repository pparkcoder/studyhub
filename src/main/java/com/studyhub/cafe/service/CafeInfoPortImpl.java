package com.studyhub.cafe.service;

import org.springframework.stereotype.Component;

import com.studyhub.cafe.domain.Seat;
import com.studyhub.cafe.repository.SeatRepository;
import com.studyhub.common.exception.BusinessException;
import com.studyhub.common.exception.CafeErrorCode;
import com.studyhub.reservation.port.CafeInfo;
import com.studyhub.reservation.port.CafeInfoPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CafeInfoPortImpl implements CafeInfoPort {

	private final SeatRepository seatRepository;

	@Override
	public CafeInfo getCafeInfo(Long cafeId, Long seatId) {
		Seat seat = seatRepository.findById(seatId)
			.orElseThrow(() -> new BusinessException(CafeErrorCode.SEAT_NOT_FOUND));
		return CafeInfo.of(seat.getCafe().getName(), seat.getSeatNumber());
	}
}

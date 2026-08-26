package com.studyhub.cafe.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.studyhub.cafe.domain.Seat;
import com.studyhub.cafe.repository.SeatRepository;
import com.studyhub.reservation.port.CafeInfo;
import com.studyhub.reservation.port.CafeInfoPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CafeInfoPortImpl implements CafeInfoPort {

	private final SeatRepository seatRepository;

	@Override
	public Map<Long, CafeInfo> getCafeInfo(List<Long> seatIds) {
		if (seatIds.isEmpty()) {
			return Map.of();
		}
		
		return seatRepository.findAllWithCafeByIdIn(seatIds)
			.stream()
			.collect(Collectors.toMap(
				Seat::getId,
				seat -> CafeInfo.of(seat.getCafe().getName(), seat.getSeatNumber())
			));
	}
}

package com.studyhub.reservation.adapter;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.studyhub.member.port.ReservationQueryPort;
import com.studyhub.reservation.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationQueryPortImpl implements ReservationQueryPort {

	private final ReservationRepository reservationRepository;

	@Override
	public boolean hasActiveReservationByMemberId(Long memberId) {
		LocalDateTime now = LocalDateTime.now();
		return reservationRepository.existsActiveByMember(memberId, now);
	}

	@Override
	public boolean hasActiveReservationByCafeId(Long cafeId) {
		LocalDateTime now = LocalDateTime.now();
		return reservationRepository.existsActiveByCafe(cafeId, now);
	}
}

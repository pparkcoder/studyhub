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
	public boolean hasActiveReservation(Long memberId) {
		LocalDateTime now = LocalDateTime.now();
		return reservationRepository.existsActiveByMember(memberId, now);
	}
}

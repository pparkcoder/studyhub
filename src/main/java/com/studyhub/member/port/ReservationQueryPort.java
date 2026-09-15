package com.studyhub.member.port;

public interface ReservationQueryPort {

	boolean hasActiveReservation(Long memberId);
}

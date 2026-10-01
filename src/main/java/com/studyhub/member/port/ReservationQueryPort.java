package com.studyhub.member.port;

public interface ReservationQueryPort {

	boolean hasActiveReservationByMemberId(Long memberId);

	boolean hasActiveReservationByCafeId(Long cafeId);
}

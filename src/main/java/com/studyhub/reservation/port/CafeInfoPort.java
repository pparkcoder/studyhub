package com.studyhub.reservation.port;

public interface CafeInfoPort {
	CafeInfo getCafeInfo(Long cafeId, Long seatId);
}

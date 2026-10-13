package com.studyhub.reservation.port;

import java.util.List;
import java.util.Map;

public interface CafeInfoPort {

	CafeInfo getCafeInfoBySeatId(Long seatId);

	Map<Long, String> getCafeNameByCafeId(List<Long> cafeIds);

}

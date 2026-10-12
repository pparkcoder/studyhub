package com.studyhub.reservation.port;

import java.util.List;
import java.util.Map;

public interface CafeInfoPort {
	Map<Long, CafeInfo> getCafeInfoBySeatIds(List<Long> seatIds);

	CafeInfo getCafeInfoBySeatId(Long seatId);

}

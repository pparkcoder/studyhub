package com.studyhub.reservation.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.studyhub.reservation.domain.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

	@Query("""
			select count(r) > 0 from Reservation r
			where r.seatId = :seatId
			and r.status = com.studyhub.reservation.domain.ReservationStatus.RESERVED
			and r.startTime > :startTime
			and r.endTime < :endTime
		""")
	boolean existsOverlapping(@Param("seatId") Long seatId,
		@Param("startTime") LocalDateTime startTime,
		@Param("endTime") LocalDateTime endTime);
}

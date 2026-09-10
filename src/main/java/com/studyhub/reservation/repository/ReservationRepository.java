package com.studyhub.reservation.repository;

import java.time.LocalDateTime;
import java.util.List;

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

	@Query("""
			select count(r) > 0 from Reservation r
			where r.memberId = :memberId
			and r.cafeId = :cafeId
			and r.status = com.studyhub.reservation.domain.ReservationStatus.RESERVED
			and r.endTime > :now
		""")
	boolean existsOverlappingByMember(@Param("memberId") Long memberId,
		@Param("cafeId") Long cafeId,
		@Param("now") LocalDateTime now
	);

	@Query("""
			select r from Reservation r
			where r.memberId = :memberId
			and r.startTime >= :startOfDay
			and r.endTime < :endOfDay
			order by r.startTime asc
		""")
	List<Reservation> findTodayReservations(@Param("memberId") Long memberId,
		@Param("startOfDay") LocalDateTime startTime,
		@Param("endOfDay") LocalDateTime endOfDay);

	@Query("""
			select r from Reservation r
			where r.memberId = :memberId
			order by r.startTime asc
		""")
	List<Reservation> findAllReservations(@Param("memberId") Long memberId);
}

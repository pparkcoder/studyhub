package com.studyhub.cafe.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.studyhub.cafe.domain.Seat;

import jakarta.persistence.LockModeType;

public interface SeatRepository extends JpaRepository<Seat, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select s from Seat s where s.id = :seatId")
	Optional<Seat> findByIdWithPessimisticLock(Long seatId);
}

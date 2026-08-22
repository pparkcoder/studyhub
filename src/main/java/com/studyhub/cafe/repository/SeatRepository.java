package com.studyhub.cafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.studyhub.cafe.domain.Seat;

public interface SeatRepository extends JpaRepository<Seat, Long> {
}

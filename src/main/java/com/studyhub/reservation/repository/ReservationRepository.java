package com.studyhub.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.studyhub.reservation.domain.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}

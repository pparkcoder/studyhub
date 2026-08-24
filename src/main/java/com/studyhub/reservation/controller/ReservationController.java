package com.studyhub.reservation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studyhub.reservation.dto.ReservationCreateRequest;
import com.studyhub.reservation.service.ReservationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {

	private final ReservationService reservationService;

	@PostMapping
	@PreAuthorize("hasRole('MEMBER')")
	public ResponseEntity<Long> reserve(
		@AuthenticationPrincipal String memberId,
		@RequestBody @Valid ReservationCreateRequest request
	) {
		return ResponseEntity.ok(reservationService.reserve(Long.valueOf(memberId), request));
	}

	@DeleteMapping("/{reservationId}")
	@PreAuthorize(("hasRole('MEMBER')"))
	public ResponseEntity<Void> cancel(
		@AuthenticationPrincipal String memberId,
		@PathVariable Long reservationId) {
		reservationService.cancel(Long.valueOf(memberId), reservationId);
		return ResponseEntity.noContent().build();
	}
}

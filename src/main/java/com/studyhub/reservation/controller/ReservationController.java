package com.studyhub.reservation.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studyhub.reservation.dto.request.ReservationCreateRequest;
import com.studyhub.reservation.dto.response.ReservationCreateResponse;
import com.studyhub.reservation.dto.response.ReservationResponse;
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
	public ResponseEntity<ReservationCreateResponse> reserve(
		@AuthenticationPrincipal String memberId,
		@RequestBody @Valid ReservationCreateRequest request) {
		ReservationCreateResponse response = reservationService.reserve(Long.valueOf(memberId), request);
		return ResponseEntity
			.created(URI.create("/reservation/" + response.getReservationId()))
			.body(response);
	}

	@DeleteMapping("/{reservationId}")
	@PreAuthorize(("hasRole('MEMBER')"))
	public ResponseEntity<Void> cancel(
		@AuthenticationPrincipal String memberId,
		@PathVariable Long reservationId) {
		reservationService.cancel(Long.valueOf(memberId), reservationId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/my")
	@PreAuthorize(("hasRole('MEMBER')"))
	public ResponseEntity<List<ReservationResponse>> findMyReservations(
		@AuthenticationPrincipal String memberId) {
		return ResponseEntity.ok(reservationService.findMyReservations(Long.valueOf(memberId)));
	}

	@GetMapping("/my/history")
	@PreAuthorize(("hasRole('MEMBER')"))
	public ResponseEntity<List<ReservationResponse>> findMyReservationsHistory(
		@AuthenticationPrincipal String memberId) {
		return ResponseEntity.ok(reservationService.findMyReservationsHistory(Long.valueOf(memberId)));
	}
}

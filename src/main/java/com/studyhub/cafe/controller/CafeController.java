package com.studyhub.cafe.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studyhub.cafe.dto.request.CafeImageRegisterRequest;
import com.studyhub.cafe.dto.request.CafeRegisterRequest;
import com.studyhub.cafe.dto.request.CafeSearchRequest;
import com.studyhub.cafe.dto.request.CafeUpdateRequest;
import com.studyhub.cafe.dto.request.SeatCreateRequest;
import com.studyhub.cafe.dto.request.SeatUpdateRequest;
import com.studyhub.cafe.dto.response.CafeDetailResponse;
import com.studyhub.cafe.dto.response.CafeImageResponse;
import com.studyhub.cafe.dto.response.CafeRegisterResponse;
import com.studyhub.cafe.dto.response.CafeSearchResponse;
import com.studyhub.cafe.dto.response.SeatResponse;
import com.studyhub.cafe.service.CafeService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cafe")
public class CafeController {

	private final CafeService cafeService;

	@PostMapping
	public ResponseEntity<CafeRegisterResponse> registerCafe(@RequestBody @Valid CafeRegisterRequest request,
		@AuthenticationPrincipal String memberId) {
		CafeRegisterResponse response = cafeService.registerCafe(request, Long.valueOf(memberId));
		return ResponseEntity
			.created(URI.create("/cafe/" + response.getCafeId()))
			.body(response);

	}

	@GetMapping
	public ResponseEntity<List<CafeSearchResponse>> searchCafes(@ModelAttribute CafeSearchRequest request) {
		return ResponseEntity.ok(cafeService.search(request));
	}

	@GetMapping("/{cafeId}")
	public ResponseEntity<CafeDetailResponse> searchCafeDetail(@PathVariable Long cafeId) {
		return ResponseEntity.ok(cafeService.searchDetail(cafeId));
	}

	@DeleteMapping("/{cafeId}")
	public ResponseEntity<Void> deleteCafe(@AuthenticationPrincipal String memberId, @PathVariable Long cafeId) {
		cafeService.deleteCafe(cafeId, Long.valueOf(memberId));
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{cafeId}")
	public ResponseEntity<Void> updateCafe(@RequestBody @Valid CafeUpdateRequest request,
		@AuthenticationPrincipal String memberId, @PathVariable Long cafeId) {
		cafeService.update(request, cafeId, Long.valueOf(memberId));
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{cafeId}/images")
	public ResponseEntity<List<CafeImageResponse>> registerCafeImages(
		@NotEmpty @RequestBody @Valid List<CafeImageRegisterRequest> requests,
		@AuthenticationPrincipal String memberId, @PathVariable Long cafeId) {
		return ResponseEntity.ok(cafeService.registerCafeImages(requests, cafeId, Long.valueOf(memberId)));
	}

	@PostMapping("/{cafeId}/seats")
	public ResponseEntity<List<SeatResponse>> registerSeats(@RequestBody @Valid SeatCreateRequest request,
		@AuthenticationPrincipal String memberId, @PathVariable Long cafeId) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(cafeService.registerSeats(request, cafeId, Long.valueOf(memberId)));
	}

	@PatchMapping("/{cafeId}/seats/{seatId}")
	public ResponseEntity<Void> updateSeat(@RequestBody @Valid SeatUpdateRequest request,
		@AuthenticationPrincipal String memberId, @PathVariable Long cafeId, @PathVariable Long seatId) {
		cafeService.updateSeat(request, cafeId, Long.valueOf(memberId), seatId);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{cafeId}/seats/{seatId}")
	public ResponseEntity<Void> deleteSeat(@AuthenticationPrincipal String memberId, @PathVariable Long cafeId,
		@PathVariable Long seatId) {
		cafeService.deleteSeat(cafeId, Long.valueOf(memberId), seatId);
		return ResponseEntity.noContent().build();
	}
}

package com.studyhub.cafe.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.studyhub.cafe.dto.request.CafeRegisterRequest;
import com.studyhub.cafe.service.CafeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CafeController {

	private final CafeService cafeService;

	@PostMapping
	public ResponseEntity<Long> regusterCafe(@RequestBody @Valid CafeRegisterRequest request,
		@AuthenticationPrincipal String memberId) {
		return ResponseEntity.ok(cafeService.registerCafe(request, Long.valueOf(memberId)));
	}

}

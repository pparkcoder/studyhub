package com.studyhub.cafe.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studyhub.cafe.dto.request.CafeRegisterRequest;
import com.studyhub.cafe.dto.request.CafeSearchRequest;
import com.studyhub.cafe.dto.response.CafeRegisterResponse;
import com.studyhub.cafe.dto.response.CafeSearchResponse;
import com.studyhub.cafe.service.CafeService;

import jakarta.validation.Valid;
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
	public ResponseEntity<List<CafeSearchResponse>> search(@ModelAttribute CafeSearchRequest request) {
		return ResponseEntity.ok(cafeService.search(request));
	}

}

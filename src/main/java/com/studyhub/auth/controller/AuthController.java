package com.studyhub.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studyhub.auth.dto.request.LoginRequest;
import com.studyhub.auth.dto.request.ReIssueRequest;
import com.studyhub.auth.dto.response.LoginResponse;
import com.studyhub.auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@AuthenticationPrincipal String memberId,
		@RequestHeader("Authorization") String header) {
		authService.logout(Long.valueOf(memberId), header);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/reissue")
	public ResponseEntity<LoginResponse> refreshToken(@RequestBody @Valid ReIssueRequest request) {
		return ResponseEntity.ok(authService.refresh(request));
	}

}

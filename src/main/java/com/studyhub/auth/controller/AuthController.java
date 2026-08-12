package com.studyhub.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

	@PostMapping("/token/refresh")
	public ResponseEntity<LoginResponse> refreshToken(@RequestBody @Valid ReIssueRequest request) {
		return ResponseEntity.ok(authService.refresh(request));
	}

}

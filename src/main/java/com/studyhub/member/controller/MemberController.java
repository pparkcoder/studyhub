package com.studyhub.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studyhub.member.dto.request.SignUpRequest;
import com.studyhub.member.dto.request.WithdrawRequest;
import com.studyhub.member.dto.response.SignUpResponse;
import com.studyhub.member.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	@PostMapping("/signup")
	public ResponseEntity<SignUpResponse> signUp(@RequestBody @Valid SignUpRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(memberService.signup(request));
	}

	@PostMapping("/withdraw")
	public ResponseEntity<Void> withdraw(@AuthenticationPrincipal String memberId,
		@Valid @RequestBody WithdrawRequest request) {
		memberService.withdraw(Long.valueOf(memberId), request);
		return ResponseEntity.noContent().build();
	}

}

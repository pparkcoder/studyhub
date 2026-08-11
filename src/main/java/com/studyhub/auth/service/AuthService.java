package com.studyhub.auth.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studyhub.auth.dto.request.LoginRequest;
import com.studyhub.auth.dto.response.LoginResponse;
import com.studyhub.common.exception.AuthErrorCode;
import com.studyhub.common.exception.BusinessException;
import com.studyhub.common.security.JwtProvider;
import com.studyhub.common.util.TokenHashUtil;
import com.studyhub.member.domain.Member;
import com.studyhub.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final MemberService memberService;
	private final PasswordEncoder passwordEncoder;
	private final JwtProvider jwtProvider;
	private final TokenHashUtil tokenHashUtil;

	@Transactional
	public LoginResponse login(LoginRequest loginRequest) {
		String username = loginRequest.getUsername();
		Member member = memberService.findByUsername(username)
			.orElseThrow(() -> new BusinessException(AuthErrorCode.LOGIN_FAILED));

		boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), member.getPassword());
		if (!passwordMatches) {
			throw new BusinessException(AuthErrorCode.LOGIN_FAILED);
		}

		String memberId = member.getId().toString();
		String memberRole = member.getRole().name();
		String accessToken = jwtProvider.createAccessToken(memberId, memberRole);
		String refreshToken = jwtProvider.createRefreshToken(memberId);
		String hash = tokenHashUtil.hash(refreshToken);
		LocalDateTime expiredAt = jwtProvider.getExpiration(refreshToken);
		member.updateRefreshToken(hash, expiredAt);
		return LoginResponse.of(accessToken, refreshToken);

	}
}

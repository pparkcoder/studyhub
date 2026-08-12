package com.studyhub.auth.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studyhub.auth.dto.request.LoginRequest;
import com.studyhub.auth.dto.request.ReIssueRequest;
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
	public LoginResponse login(LoginRequest request) {
		String username = request.getUsername();
		Member member = memberService.findByUsername(username)
			.orElseThrow(() -> new BusinessException(AuthErrorCode.LOGIN_FAILED));

		boolean passwordMatches = passwordEncoder.matches(request.getPassword(), member.getPassword());
		if (!passwordMatches) {
			throw new BusinessException(AuthErrorCode.LOGIN_FAILED);
		}
		return createToken(member);
	}

	@Transactional
	public LoginResponse refresh(ReIssueRequest request) {
		String refreshToken = request.getRefreshToken();
		// 토큰 유효성 검증
		boolean validateToken = jwtProvider.validateToken(refreshToken);
		if (!validateToken) {
			throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN);
		}

		// 회원 유무 검증
		Member member = memberService.findById(Long.valueOf(jwtProvider.getMemberId(refreshToken)))
			.orElseThrow(() -> new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN));

		// 토큰 해시 검증
		String newHash = tokenHashUtil.hash(refreshToken);
		if (!newHash.equals(member.getRefreshTokenHash())) {
			member.clearRefreshToken();
			throw new BusinessException(AuthErrorCode.REFRESH_TOKEN_MISMATCH);
		}

		// 토큰 만료 검증
		LocalDateTime refreshTokenExpiredAt = member.getRefreshTokenExpiredAt();
		if (refreshTokenExpiredAt.isBefore(LocalDateTime.now())) {
			member.clearRefreshToken();
			throw new BusinessException(AuthErrorCode.EXPIRED_REFRESH_TOKEN);
		}
		return createToken(member);
	}

	private LoginResponse createToken(Member member) {
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

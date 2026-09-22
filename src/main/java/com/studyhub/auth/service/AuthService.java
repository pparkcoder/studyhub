package com.studyhub.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studyhub.auth.dto.request.LoginRequest;
import com.studyhub.auth.dto.request.ReIssueRequest;
import com.studyhub.auth.dto.response.LoginResponse;
import com.studyhub.common.exception.AuthErrorCode;
import com.studyhub.common.exception.BusinessException;
import com.studyhub.common.security.JwtProvider;
import com.studyhub.common.security.RefreshTokenStore;
import com.studyhub.common.security.TokenInvalidator;
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
	private final TokenInvalidator tokenInvalidator;
	private final RefreshTokenStore refreshTokenStore;

	@Transactional(readOnly = true)
	public LoginResponse login(LoginRequest request) {
		String username = request.getUsername();
		Member member = memberService.findByUsername(username)
			.orElseThrow(() -> new BusinessException(AuthErrorCode.LOGIN_FAILED));

		// 패스워드 검증
		boolean passwordMatches = passwordEncoder.matches(request.getPassword(), member.getPassword());
		if (!passwordMatches) {
			throw new BusinessException(AuthErrorCode.LOGIN_FAILED);
		}

		// 탈퇴 여부 검증
		if (member.isWithdrawn()) {
			throw new BusinessException(AuthErrorCode.LOGIN_FAILED); // 탈퇴 여부를 알려주지 않음
		}
		return createToken(member);
	}

	public void logout(Long memberId, String header) {
		tokenInvalidator.invalidate(memberId, header);
	}

	@Transactional(readOnly = true)
	public LoginResponse refresh(ReIssueRequest request) {
		String refreshToken = request.getRefreshToken();
		// 토큰 유효성 검증
		boolean validateToken = jwtProvider.validateToken(refreshToken);
		if (!validateToken) {
			throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN);
		}

		// 토큰 해시 검증
		Long memberId = Long.valueOf(jwtProvider.getMemberId(refreshToken));
		String storedHash = refreshTokenStore.find(memberId)
			.orElseThrow(() -> new BusinessException(AuthErrorCode.EXPIRED_REFRESH_TOKEN));
		if (!tokenHashUtil.hash(refreshToken).equals(storedHash)) {
			refreshTokenStore.delete(memberId);
			throw new BusinessException(AuthErrorCode.REFRESH_TOKEN_MISMATCH);
		}

		// 회원 유무 검증
		Member member = memberService.findById(memberId)
			.orElseThrow(() -> new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN));

		// 탈퇴 여부 검증
		if (member.isWithdrawn()) {
			throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN); // 로그인 시도가 아니므로 다른 에러코드 사용
		}

		return createToken(member);
	}

	private LoginResponse createToken(Member member) {
		String memberId = member.getId().toString();
		String memberRole = member.getRole().name();

		String accessToken = jwtProvider.createAccessToken(memberId, memberRole);
		String refreshToken = jwtProvider.createRefreshToken(memberId);

		String hash = tokenHashUtil.hash(refreshToken);
		long refreshTokenExpiration = jwtProvider.getRefreshTokenExpiration();

		refreshTokenStore.save(member.getId(), hash, refreshTokenExpiration);

		return LoginResponse.of(accessToken, refreshToken);
	}
}

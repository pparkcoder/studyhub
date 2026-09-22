package com.studyhub.common.security;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenInvalidator {

	private final JwtProvider jwtProvider;
	private final TokenBlacklist tokenBlacklist;
	private final RefreshTokenStore refreshTokenStore;

	public void invalidate(Long memberId, String header) {
		String token = jwtProvider.resolveToken(header);
		String jti = jwtProvider.getJti(token);
		long ttlMillis = jwtProvider.getRemainingMillis(token);
		tokenBlacklist.addBlacklist(jti, ttlMillis);
		refreshTokenStore.delete(memberId);
	}
}

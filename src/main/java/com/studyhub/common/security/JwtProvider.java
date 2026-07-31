package com.studyhub.common.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProvider {

	private final SecretKey secretKey;
	private final long accessTokenExpiration;
	private final long refreshTokenExpiration;

	public JwtProvider(
		@Value("${jwt.secret}") String secret,
		@Value("${jwt.access-token-expiration}") long accessTokenExpiration,
		@Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration) {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.accessTokenExpiration = accessTokenExpiration;
		this.refreshTokenExpiration = refreshTokenExpiration;
	}

	public String createAccessToken(String memberId, String role) {
		if (role == null || role.isBlank()) {
			throw new IllegalArgumentException("role은 필수입니다.");
		}
		return createToken(memberId, role, accessTokenExpiration);
	}

	public String createRefreshToken(String memberId) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + refreshTokenExpiration);

		return Jwts.builder()
			.subject(memberId)
			.issuedAt(now)
			.expiration(expiry)
			.signWith(secretKey)
			.compact();
	}

	private String createToken(String memberId, String role, long expirationMillis) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + expirationMillis);

		return Jwts.builder()
			.subject(memberId)
			.claim("role", role)
			.issuedAt(now)
			.expiration(expiry)
			.signWith(secretKey)
			.compact();
	}

	public boolean validateToken(String token) {
		try {
			parseClaims(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	private Claims parseClaims(String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}

	public String getMemberId(String token) {
		return parseClaims(token).getSubject();
	}

	public String getRole(String token) {
		return parseClaims(token).get("role", String.class);
	}

}

package com.studyhub.common.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtProvider {

	private final SecretKey secretKey;
	private final long accessTokenExpiration;
	private final long refreshTokenExpiration;

	public JwtProvider(
		@Value("${jwt.secret}") String secret,
		@Value("${jwt.secret}") long accessTokenExpiration,
		@Value("${jwt.secret}") long refreshTokenExpiration) {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.accessTokenExpiration = accessTokenExpiration;
		this.refreshTokenExpiration = refreshTokenExpiration;
	}

	public String createAccessToken(String memberId, String role) {
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
		try{
			parseClaims(token);
			return true;
		} catch(ExpiredJwtException e) {
			return false;
		} catch(SignatureException e) {
			return false;
		} catch (JwtException | IllegalArgumentException e){
			return false;
		}
	}

	private Claims parseClaims(String token){
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}

	public String getMember(String token) {
		return parseClaims(token).getSubject();
	}

	public String getRole(String token) {
		return parseClaims(token).get("role", String.class);
	}

}

package com.common.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";
	private final JwtProvider jwtProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String token = resolveToken(request);
		if(token != null && jwtProvider.validateToken(token)) {
			String memberId = jwtProvider.getMember(token);
			String role = jwtProvider.getRole(token);

			if(role != null){
				List<SimpleGrantedAuthority> authorities = List.of(
					new SimpleGrantedAuthority("ROLE_" + role));

				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					memberId, null, authorities);

				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}

		// 토큰이 없거나 유효하지 않아도 체인은 계속 진행
		// 최종적으로 막을지 말지는 SecurityConfig의 authorizeHttpRequests 규칙이 결정
		filterChain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

		if(bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
			return bearerToken.substring(BEARER_PREFIX.length());
		}

		return null;
	}
}

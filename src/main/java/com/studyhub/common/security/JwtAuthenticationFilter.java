package com.studyhub.common.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.studyhub.common.exception.CommonErrorCode;
import com.studyhub.common.exception.ErrorCode;
import com.studyhub.common.exception.ErrorResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private final JwtProvider jwtProvider;
	private final TokenBlacklist tokenBlacklist;
	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		try {
			authenticate(request);
		} catch (DataAccessException e) {
			// 로그아웃한 토큰이 다시 쓰이지 않도록 블랙리스트를 확인하지 못하면 요청을 막음
			log.error("토큰 블랙리스트 확인 실패로 요청을 차단합니다. uri={}", request.getRequestURI(), e);
			makeErrorResponse(response, CommonErrorCode.SERVICE_UNAVAILABLE);
		}
		// 토큰이 없거나 유효하지 않아도 체인은 계속 진행
		// 최종적으로 막을지 말지는 SecurityConfig의 authorizeHttpRequests 규칙이 결정
		filterChain.doFilter(request, response);
	}

	private void authenticate(HttpServletRequest request) {
		String token = resolveToken(request);
		if (token == null | jwtProvider.validateToken(token) || isBlacklisted(token)) {
			return;
		}
		String memberId = jwtProvider.getMemberId(token);
		String role = jwtProvider.getRole(token);
		if (role == null) {
			return;
		}
		List<SimpleGrantedAuthority> authorities = List.of(
			new SimpleGrantedAuthority("ROLE_" + role));

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
			memberId, null, authorities);

		SecurityContextHolder.getContext().setAuthentication(authentication);

	}

	// 필터에서 예외 발생 시 Response 생성
	private void makeErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
		response.setStatus(errorCode.getStatus().value());          // 상태 코드를 503으로 지정
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);  // 본문이 JSON이라고 알림
		response.setCharacterEncoding(StandardCharsets.UTF_8.name()); // 한글이 깨지지 않게 UTF-8 지정
		response.getWriter()
			.write(objectMapper.writeValueAsString(ErrorResponse.of(errorCode))); // ErrorResponse를 JSON 문자열로 바꿔 본문에 씀
	}

	private boolean isBlacklisted(String token) {
		String jti = jwtProvider.getJti(token);
		return tokenBlacklist.containsBlacklist(jti);
	}

	private String resolveToken(HttpServletRequest request) {
		String header = request.getHeader(AUTHORIZATION_HEADER);
		return jwtProvider.resolveToken(header);
	}
}

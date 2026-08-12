package com.studyhub.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginResponse {

	private String accessToken;
	private String refreshToken;

	public static LoginResponse of(String accessToken, String refreshToken) {
		return LoginResponse.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}
	
}

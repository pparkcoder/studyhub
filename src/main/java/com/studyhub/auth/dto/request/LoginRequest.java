package com.studyhub.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class LoginRequest {

	@NotBlank
	@NotNull
	@Size(max = 20)
	private String username;

	@NotBlank
	@NotNull
	private String password;
}

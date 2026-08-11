package com.studyhub.member.dto.request;

import com.studyhub.member.domain.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SignUpRequest {

	@NotBlank
	@Size(max = 20)
	private String username;

	@NotBlank
	@Size(max = 20)
	private String nickname;

	@NotBlank
	@Size(max = 50)
	private String email;

	@NotBlank
	private String password;

	@NotNull
	private Role role;
}

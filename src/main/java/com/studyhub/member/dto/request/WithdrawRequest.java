package com.studyhub.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class WithdrawRequest {

	@NotBlank
	private String password;
}

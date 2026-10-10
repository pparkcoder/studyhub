package com.studyhub.cafe.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CafeImageRegisterRequest {

	private Long id;

	@NotBlank
	private String imageUrl;

	@NotNull
	@Min(0)
	private Integer sortOrder;
}

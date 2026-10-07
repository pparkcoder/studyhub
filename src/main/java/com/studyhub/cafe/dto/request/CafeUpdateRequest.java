package com.studyhub.cafe.dto.request;

import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CafeUpdateRequest {

	@NotBlank
	@Size(max = 50)
	private String name;

	@NotBlank
	@Size(max = 100)
	private String address;

	@NotNull
	private String region;

	@NotNull
	private Double latitude;

	@NotBlank
	private Double longitude;

	@NotNull
	private LocalTime openTime;

	@NotNull
	private LocalTime closeTime;
}

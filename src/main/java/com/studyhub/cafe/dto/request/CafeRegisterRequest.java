package com.studyhub.cafe.dto.request;

import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CafeRegisterRequest {

	@NotBlank
	@Size(max = 50)
	private String name;

	@NotBlank
	@Size(max = 100)
	private String address;

	@NotBlank
	private String region;

	@NotBlank
	private Double latitude;

	@NotBlank
	private Double longitude;

	@NotBlank
	private LocalTime openTime;

	@NotBlank
	private LocalTime closeTime;
}

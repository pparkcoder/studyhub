package com.studyhub.cafe.dto.request;

import java.time.LocalTime;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

	@NotNull
	private Double latitude;

	@NotNull
	private Double longitude;

	@NotNull
	private LocalTime openTime;

	@NotNull
	private LocalTime closeTime;

	@NotNull
	@Min(1)
	private Integer seatCount;

	private List<String> imageUrls;
}

package com.studyhub.cafe.dto.request;

import java.time.LocalTime;
import java.util.List;

import jakarta.validation.constraints.Min;
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

	@NotBlank
	@Min(1)
	private Integer seatCount;

	private List<String> imageUrls;
}

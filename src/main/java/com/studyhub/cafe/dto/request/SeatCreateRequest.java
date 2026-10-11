package com.studyhub.cafe.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SeatCreateRequest {

	@NotNull
	@Min(1)
	@Max(1000)
	private Integer seatCount;
}

package com.studyhub.cafe.dto.request;

import com.studyhub.cafe.domain.SeatStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SeatUpdateRequest {
	@NotNull
	private SeatStatus status;

}

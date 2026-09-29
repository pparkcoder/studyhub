package com.studyhub.cafe.dto.response;

import java.util.List;

import com.studyhub.cafe.domain.Cafe;

import lombok.Getter;

@Getter
public class CafeDetailResponse {

	private final CafeSearchResponse cafe;
	private final List<CafeImageResponse> images;
	private final List<SeatResponse> seats;

	private CafeDetailResponse(CafeSearchResponse cafe, List<CafeImageResponse> images, List<SeatResponse> seats) {
		this.cafe = cafe;
		this.images = images;
		this.seats = seats;
	}

	public static CafeDetailResponse from(Cafe cafe) {
		return new CafeDetailResponse(
			CafeSearchResponse.from(cafe),
			cafe.getImages().stream()
				.map(CafeImageResponse::from)
				.toList(),
			cafe.getSeats().stream()
				.map(SeatResponse::from)
				.toList()
		);
	}
}

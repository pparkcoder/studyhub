package com.studyhub.cafe.dto.response;

import java.util.List;

import com.studyhub.cafe.domain.Cafe;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CafeDetailResponse {

	private CafeSearchResponse cafe;
	private List<CafeImageResponse> images;
	private List<SeatResponse> seats;

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

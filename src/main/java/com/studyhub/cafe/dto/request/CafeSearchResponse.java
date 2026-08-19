package com.studyhub.cafe.dto.request;

import com.studyhub.cafe.domain.Cafe;

import lombok.Getter;

@Getter
public class CafeSearchResponse {

	private Long id;
	private String name;
	private String address;
	private String region;
	private String mainImageUrl;
	private int seatCount;

	private CafeSearchResponse(Long id, String name, String address, String region, String mainImageUrl,
		int seatCount) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.region = region;
		this.mainImageUrl = mainImageUrl;
		this.seatCount = seatCount;
	}

	public static CafeSearchResponse from(Cafe cafe) {
		String mainImageUrl = cafe.getImages().isEmpty() ? null : cafe.getImages().get(0).getImageUrl();

		return new CafeSearchResponse(
			cafe.getId(),
			cafe.getName(),
			cafe.getAddress(),
			cafe.getRegion(),
			mainImageUrl,
			cafe.getSeats().size()
		);
	}
}

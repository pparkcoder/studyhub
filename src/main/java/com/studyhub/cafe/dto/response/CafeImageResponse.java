package com.studyhub.cafe.dto.response;

import com.studyhub.cafe.domain.CafeImage;

import lombok.Getter;

@Getter
public class CafeImageResponse {

	private Long id;
	private String imageUrl;

	private CafeImageResponse(Long id, String imageUrl) {
		this.id = id;
		this.imageUrl = imageUrl;
	}

	public static CafeImageResponse from(CafeImage image) {
		return new CafeImageResponse(
			image.getId(),
			image.getImageUrl()
		);
	}
}

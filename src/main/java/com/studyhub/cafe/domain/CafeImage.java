package com.studyhub.cafe.domain;

import com.studyhub.common.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cafe_image")
public class CafeImage extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "image_url", nullable = false)
	private String imageUrl;

	@Column(name = "sort_order", nullable = false)
	private Integer sortOrder;

	@Builder(access = AccessLevel.PRIVATE)
	private CafeImage(String imageUrl, Integer sortOrder) {
		this.imageUrl = imageUrl;
		this.sortOrder = sortOrder;
	}

	public static CafeImage of(String imageUrl, Integer sortOrder) {
		return CafeImage.builder()
			.imageUrl(imageUrl)
			.sortOrder(sortOrder)
			.build();
	}
}

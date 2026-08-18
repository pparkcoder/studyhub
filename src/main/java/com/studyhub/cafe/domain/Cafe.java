package com.studyhub.cafe.domain;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.studyhub.cafe.dto.request.CafeRegisterRequest;
import com.studyhub.common.entity.BaseTimeEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cafe")
public class Cafe extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 50, nullable = false)
	private String name;

	@Column(length = 100, nullable = false)
	private String address;

	@Column(length = 20, nullable = false)
	private String region;

	@Column(nullable = false)
	private Double latitude; // 위도

	@Column(nullable = false)
	private Double longitude; // 경도

	@Column(nullable = false)
	private LocalTime openTime;

	@Column(nullable = false)
	private LocalTime closeTime;

	@Column(name = "owner_id", nullable = false)
	private Long ownerId;

	@OneToMany(mappedBy = "cafe", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Seat> seats = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "cafe_id")
	@OrderBy("sortOrder ASC")
	private List<CafeImage> images = new ArrayList<>();

	@Builder(access = AccessLevel.PRIVATE)
	private Cafe(String name, String address, String region, Double latitude, Double longitude, LocalTime openTime,
		LocalTime closeTime, Long ownerId) {
		this.name = name;
		this.address = address;
		this.region = region;
		this.latitude = latitude;
		this.longitude = longitude;
		this.openTime = openTime;
		this.closeTime = closeTime;
		this.ownerId = ownerId;
	}

	public static Cafe from(CafeRegisterRequest request, Long onwerId) {
		return Cafe.builder()
			.name(request.getName())
			.address(request.getAddress())
			.region(request.getRegion())
			.latitude(request.getLatitude())
			.longitude(request.getLatitude())
			.openTime(request.getOpenTime())
			.closeTime(request.getCloseTime())
			.ownerId(onwerId)
			.build();
	}

	public void addSeat(Seat seat) {
		this.seats.add(seat);
		seat.assignCafe(this);
	}

	public void addImage(CafeImage image) {
		this.images.add(image);
	}

}

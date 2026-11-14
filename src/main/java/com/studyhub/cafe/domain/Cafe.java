package com.studyhub.cafe.domain;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.hibernate.annotations.Comment;

import com.studyhub.cafe.dto.request.CafeRegisterRequest;
import com.studyhub.cafe.dto.request.CafeUpdateRequest;
import com.studyhub.cafe.port.ResolvedAddress;
import com.studyhub.common.entity.BaseTimeEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

	@Comment("시/군/구 코드")
	@Column(length = 5, nullable = false)
	private String regionCode;

	@Comment("위도")
	@Column(nullable = false)
	private Double latitude;

	@Comment("경도")
	@Column(nullable = false)
	private Double longitude;

	@Column(nullable = false)
	private LocalTime openTime;

	@Column(nullable = false)
	private LocalTime closeTime;

	@Column(name = "owner_id", nullable = false)
	private Long ownerId;

	@OneToMany(mappedBy = "cafe", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Seat> seats = new ArrayList<>();

	@OneToMany(mappedBy = "cafe", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("sortOrder ASC")
	private List<CafeImage> images = new ArrayList<>();

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private CafeStatus status;

	@Builder(access = AccessLevel.PRIVATE)
	private Cafe(String name, String address, String region, String regionCode, Double latitude, Double longitude,
		LocalTime openTime,
		LocalTime closeTime, Long ownerId) {
		this.name = name;
		this.address = address;
		this.region = region;
		this.regionCode = regionCode;
		this.latitude = latitude;
		this.longitude = longitude;
		this.openTime = openTime;
		this.closeTime = closeTime;
		this.ownerId = ownerId;
		this.status = CafeStatus.ACTIVE;
	}

	public static Cafe of(CafeRegisterRequest request, ResolvedAddress resolvedAddress, Long ownerId) {
		return Cafe.builder()
			.name(request.getName())
			.address(request.getAddress())
			.region(resolvedAddress.regionName())
			.regionCode(resolvedAddress.sigunguCode())
			.latitude(resolvedAddress.latitude())
			.longitude(resolvedAddress.longitude())
			.openTime(request.getOpenTime())
			.closeTime(request.getCloseTime())
			.ownerId(ownerId)
			.build();
	}

	public void addSeat(Seat seat) {
		this.seats.add(seat);
		seat.assignCafe(this);
	}

	public void addImage(CafeImage image) {
		this.images.add(image);
		image.assignCafe(this);
	}

	public void deleteImage(Set<Long> imageIds) {
		images.removeIf(image -> !imageIds.contains(image.getId()));
	}

	public boolean isOwnedBy(Long memberId) {
		return ownerId.equals(memberId);
	}

	public void delete() {
		this.status = CafeStatus.DELETED;
	}

	public boolean isDeleted() {
		return status == CafeStatus.DELETED;
	}

	public void update(CafeUpdateRequest request, ResolvedAddress resolvedAddress) {
		this.name = request.getName();
		this.address = request.getAddress();
		this.region = resolvedAddress.regionName();
		this.regionCode = resolvedAddress.sigunguCode();
		this.latitude = resolvedAddress.latitude();
		this.longitude = resolvedAddress.longitude();
		this.openTime = request.getOpenTime();
		this.closeTime = request.getCloseTime();
	}

	public boolean hasImageWithSortOrder(Integer sortOrder) {
		return images.stream()
			.anyMatch(image -> Objects.equals(image.getSortOrder(), sortOrder));
	}

	public void deleteSeat(Seat seat) {
		seats.remove(seat);
	}

}

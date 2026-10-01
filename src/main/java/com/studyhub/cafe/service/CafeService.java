package com.studyhub.cafe.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studyhub.cafe.domain.Cafe;
import com.studyhub.cafe.domain.CafeImage;
import com.studyhub.cafe.domain.Seat;
import com.studyhub.cafe.dto.request.CafeRegisterRequest;
import com.studyhub.cafe.dto.request.CafeSearchRequest;
import com.studyhub.cafe.dto.response.CafeDetailResponse;
import com.studyhub.cafe.dto.response.CafeRegisterResponse;
import com.studyhub.cafe.dto.response.CafeSearchResponse;
import com.studyhub.cafe.port.OwnerValidator;
import com.studyhub.cafe.repository.CafeRepository;
import com.studyhub.common.exception.BusinessException;
import com.studyhub.common.exception.CafeErrorCode;
import com.studyhub.member.port.ReservationQueryPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CafeService {

	private final OwnerValidator ownerValidator;
	private final CafeRepository cafeRepository;
	private final ReservationQueryPort reservationQueryPort;

	@Transactional
	public CafeRegisterResponse registerCafe(CafeRegisterRequest request, Long memberId) {
		validateOwner(memberId);
		Cafe cafe = Cafe.from(request, memberId);
		addSeats(cafe, request.getSeatCount());
		addImages(cafe, request.getImageUrls());

		Cafe saveCafe = cafeRepository.save(cafe);
		return CafeRegisterResponse.from(saveCafe);
	}

	@CacheEvict(value = "cafe", key = "#cafeId")
	@Transactional
	public void deleteCafe(Long cafeId, Long memberId) {
		Cafe cafe = validateCafe(cafeId);
		if (!cafe.isOwnedBy(memberId)) {
			throw new BusinessException(CafeErrorCode.NOT_CAFE_OWNER);
		}
		cafe.delete();
	}

	@Transactional(readOnly = true)
	public List<CafeSearchResponse> search(CafeSearchRequest request) {
		List<Cafe> result = cafeRepository.search(request);
		return result.stream().map(CafeSearchResponse::from).toList();
	}

	@Cacheable(value = "cafe", key = "#cafeId")
	@Transactional(readOnly = true)
	public CafeDetailResponse searchDetail(Long cafeId) {
		Cafe cafe = validateCafe(cafeId);
		return CafeDetailResponse.from(cafe);
	}

	private void validateOwner(Long memberId) {
		CafeErrorCode errorCode = switch (ownerValidator.validate(memberId)) {
			case NOT_FOUND -> CafeErrorCode.OWNER_NOT_FOUND;
			case WITHDRAWN -> CafeErrorCode.OWNER_WITHDRAWN;
			case NOT_OWNER_ROLE -> CafeErrorCode.NOT_OWNER_ROLE;
			case VALID -> null;
		};
		if (errorCode != null) {
			throw new BusinessException(errorCode);
		}
	}

	private Cafe validateCafe(Long cafeId) {
		Cafe cafe = cafeRepository.findById(cafeId)
			.orElseThrow(() -> new BusinessException(CafeErrorCode.CAFE_NOT_FOUND));
		if (cafe.isDeleted()) {
			throw new BusinessException(CafeErrorCode.ALREADY_DELETED);
		}
		return cafe;
	}

	private void addSeats(Cafe cafe, int seatCount) {
		for (int i = 1; i <= seatCount; ++i) {
			Seat seat = Seat.of("A" + i);
			cafe.addSeat(seat);
		}
	}

	private void addImages(Cafe cafe, List<String> imageUrls) {
		if (imageUrls == null) {
			return;
		}
		for (int i = 0; i < imageUrls.size(); ++i) {
			CafeImage cafeImage = CafeImage.of(imageUrls.get(i), i);
			cafe.addImage(cafeImage);
		}
	}
}

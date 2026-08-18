package com.studyhub.cafe.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.studyhub.cafe.domain.Cafe;
import com.studyhub.cafe.domain.CafeImage;
import com.studyhub.cafe.domain.Seat;
import com.studyhub.cafe.dto.request.CafeRegisterRequest;
import com.studyhub.cafe.port.OwnerValidator;
import com.studyhub.cafe.repository.CafeRepository;
import com.studyhub.common.exception.BusinessException;
import com.studyhub.common.exception.CafeErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CafeService {

	private final OwnerValidator ownerValidator;
	private final CafeRepository cafeRepository;

	public Long registerCafe(CafeRegisterRequest request, Long memberId) {
		validateOwner(memberId);
		Cafe cafe = Cafe.from(request, memberId);
		addSeats(cafe, request.getSeatCount());
		addImages(cafe, request.getImageUrls());
		return cafeRepository.save(cafe).getId();
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

package com.studyhub.cafe.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studyhub.cafe.domain.Cafe;
import com.studyhub.cafe.domain.CafeImage;
import com.studyhub.cafe.domain.Seat;
import com.studyhub.cafe.dto.request.CafeImageRegisterRequest;
import com.studyhub.cafe.dto.request.CafeRegisterRequest;
import com.studyhub.cafe.dto.request.CafeSearchRequest;
import com.studyhub.cafe.dto.request.CafeUpdateRequest;
import com.studyhub.cafe.dto.request.SeatCreateRequest;
import com.studyhub.cafe.dto.request.SeatUpdateRequest;
import com.studyhub.cafe.dto.response.CafeDetailResponse;
import com.studyhub.cafe.dto.response.CafeImageResponse;
import com.studyhub.cafe.dto.response.CafeRegisterResponse;
import com.studyhub.cafe.dto.response.CafeSearchResponse;
import com.studyhub.cafe.dto.response.SeatResponse;
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

	@Transactional
	public CafeRegisterResponse registerCafe(CafeRegisterRequest request, Long memberId) {
		validateOwner(memberId);
		Cafe cafe = Cafe.from(request, memberId);
		int lastSeatNumber = 0;
		int newLastSeatNumber = lastSeatNumber + request.getSeatCount();
		addSeats(cafe, lastSeatNumber, newLastSeatNumber);
		addImages(cafe, request.getImageUrls());

		Cafe saveCafe = cafeRepository.save(cafe);
		return CafeRegisterResponse.from(saveCafe);
	}

	@CacheEvict(value = "cafe", key = "#cafeId")
	@Transactional
	public void deleteCafe(Long cafeId, Long memberId) {
		Cafe cafe = getOwnedCafe(cafeId, memberId);
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

	@CacheEvict(value = "cafe", key = "#cafeId")
	@Transactional
	public void update(CafeUpdateRequest request, Long cafeId, Long memberId) {
		Cafe cafe = getOwnedCafe(cafeId, memberId);
		cafe.update(request);
	}

	@CacheEvict(value = "cafe", key = "#cafeId")
	@Transactional
	public List<CafeImageResponse> registerCafeImages(List<CafeImageRegisterRequest> requests, Long cafeId,
		Long memberId) {
		Cafe cafe = getOwnedCafe(cafeId, memberId);
		validateSortOrders(requests);

		Map<Long, CafeImage> cafeImageById = cafe.getImages()
			.stream()
			.collect(Collectors.toMap(CafeImage::getId, Function.identity()));

		Set<Long> requestIds = requests.stream()
			.map(CafeImageRegisterRequest::getId)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());

		for (Long requestId : requestIds) {
			if (!cafeImageById.containsKey(requestId)) {
				String message = String.format("ID : %s 해당 카페에 존재하지 않는 이미지 입니다.", requestId);
				throw new BusinessException(CafeErrorCode.IMAGE_NOT_FOUND, message);
			}
		}
		cafe.deleteImage(requestIds);

		for (CafeImageRegisterRequest request : requests) {
			if (request.getId() == null) { // 추가
				CafeImage cafeImage = CafeImage.of(request.getImageUrl(), request.getSortOrder());
				cafe.addImage(cafeImage);
			} else { // 수정
				CafeImage cafeImage = cafeImageById.get(request.getId());
				cafeImage.updateImage(request.getImageUrl(), request.getSortOrder());
			}
		}
		cafeRepository.flush();
		return cafe.getImages()
			.stream()
			.sorted(Comparator.comparing(CafeImage::getSortOrder))
			.map(CafeImageResponse::from)
			.toList();
	}

	@CacheEvict(value = "cafe", key = "#cafeId")
	@Transactional
	public List<SeatResponse> registerSeats(SeatCreateRequest request, Long cafeId, Long memberId) {
		Cafe cafe = getOwnedCafe(cafeId, memberId);
		int lastSeatNumber = findLastSeatNumber(cafe);
		int newLastSeatNumber = lastSeatNumber + request.getSeatCount();
		List<Seat> seats = addSeats(cafe, lastSeatNumber, newLastSeatNumber);
		cafeRepository.flush();
		return seats.stream()
			.map(SeatResponse::from)
			.toList();

	}

	@CacheEvict(value = "cafe", key = "#cafeId")
	@Transactional
	public void updateSeat(SeatUpdateRequest request, Long cafeId, Long memberId, Long seatId) {
		Cafe cafe = getOwnedCafe(cafeId, memberId);
		Seat seat = findSeatById(cafe, seatId);
		switch (request.getStatus()) {
			case AVAILABLE -> seat.enable();
			case DISABLED -> seat.disable();
		}
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

	private Cafe getOwnedCafe(Long cafeId, Long memberId) {
		validateOwner(memberId);
		Cafe cafe = validateCafe(cafeId);
		if (!cafe.isOwnedBy(memberId)) {
			throw new BusinessException(CafeErrorCode.NOT_CAFE_OWNER);
		}
		return cafe;
	}

	private Cafe validateCafe(Long cafeId) {
		Cafe cafe = cafeRepository.findById(cafeId)
			.orElseThrow(() -> new BusinessException(CafeErrorCode.CAFE_NOT_FOUND));
		if (cafe.isDeleted()) {
			throw new BusinessException(CafeErrorCode.ALREADY_DELETED);
		}
		return cafe;
	}

	private List<Seat> addSeats(Cafe cafe, int startSeatCount, int endSeatCount) {
		List<Seat> result = new ArrayList<>();
		for (int i = startSeatCount + 1; i <= endSeatCount; ++i) {
			Seat seat = Seat.of("A" + i);
			cafe.addSeat(seat);
			result.add(seat);
		}
		return result;
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

	private void validateSortOrders(List<CafeImageRegisterRequest> requests) {
		Set<Integer> sortOrders = new HashSet<>();
		for (CafeImageRegisterRequest request : requests) {
			Integer sortOrder = request.getSortOrder();
			if (!sortOrders.add(sortOrder)) {
				throw new BusinessException(CafeErrorCode.DUPLICATE_SORT_ORDER);
			}
		}
	}

	private int findLastSeatNumber(Cafe cafe) {
		return cafe.getSeats().stream()
			.mapToInt(seat -> Integer.parseInt(seat.getSeatNumber().substring(1)))
			.max()
			.orElse(0);
	}

	private Seat findSeatById(Cafe cafe, Long seatId) {
		return cafe.getSeats().stream()
			.filter(seat -> seat.getId().equals(seatId))
			.findFirst()
			.orElseThrow(() -> new BusinessException(CafeErrorCode.SEAT_NOT_FOUND));
	}
}

package com.studyhub.reservation.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studyhub.cafe.service.CafeInfoPortImpl;
import com.studyhub.common.exception.BusinessException;
import com.studyhub.common.exception.ReservationErrorCode;
import com.studyhub.reservation.domain.Reservation;
import com.studyhub.reservation.domain.ReservationDuration;
import com.studyhub.reservation.dto.request.ReservationCreateRequest;
import com.studyhub.reservation.dto.response.ReservationCreateResponse;
import com.studyhub.reservation.dto.response.ReservationResponse;
import com.studyhub.reservation.port.CafeInfo;
import com.studyhub.reservation.port.CafeLockPort;
import com.studyhub.reservation.port.MemberValidator;
import com.studyhub.reservation.port.SeatValidator;
import com.studyhub.reservation.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {

	private final MemberValidator memberValidator;
	private final SeatValidator seatValidator;
	private final CafeLockPort cafeLockPort;
	private final ReservationRepository reservationRepository;
	private final CafeInfoPortImpl cafeInfoPort;

	@Transactional
	public ReservationCreateResponse reserve(Long memberId, ReservationCreateRequest request) {
		Long cafeId = request.getCafeId();
		Long seatId = request.getSeatId();
		LocalDateTime startTime = request.getStartTime();

		ReservationDuration duration = request.getDuration();

		validateMember(memberId);
		validateSeat(cafeId, seatId);
		validateStartTime(startTime);

		cafeLockPort.lock(cafeId);

		LocalDateTime endTime = duration.calculateEndTime(startTime);

		// 같은 카페에 시간이 겹치는 내 예약이 있는지
		boolean existsOverlappingByMember = reservationRepository.existsOverlappingByMember(memberId, cafeId, startTime,
			endTime);
		if (existsOverlappingByMember) {
			throw new BusinessException(ReservationErrorCode.ALREADY_RESERVED_IN_CAFE);
		}

		// 해당 좌석의 시간이 겹치는 예약이 있는지
		boolean existsOverlapping = reservationRepository.existsOverlapping(seatId, startTime, endTime);
		if (existsOverlapping) {
			throw new BusinessException(ReservationErrorCode.ALREADY_RESERVED);
		}

		Reservation reserve = Reservation.reserve(memberId, cafeId, seatId, startTime, duration);
		Reservation savedReservation = reservationRepository.save(reserve);
		return ReservationCreateResponse.from(savedReservation);
	}

	private void validateMember(Long memberId) {
		ReservationErrorCode errorCode = switch (memberValidator.validate(memberId)) {
			case NOT_FOUND -> ReservationErrorCode.MEMBER_NOT_FOUND;
			case WITHDRAWN -> ReservationErrorCode.WITHDRAWN_MEMBER;
			case VALID -> null;
		};
		if (errorCode != null) {
			throw new BusinessException(errorCode);
		}
	}

	private void validateSeat(Long cafeId, Long seatId) {
		ReservationErrorCode errorCode = switch (seatValidator.validate(cafeId, seatId)) {
			case NOT_FOUND -> ReservationErrorCode.SEAT_NOT_FOUND;
			case CAFE_MISMATCH -> ReservationErrorCode.SEAT_CAFE_MISMATCH;
			case DISABLED -> ReservationErrorCode.SEAT_DISABLED;
			case VALID -> null;
		};
		if (errorCode != null) {
			throw new BusinessException(errorCode);
		}
	}

	private void validateStartTime(LocalDateTime startTime) {
		LocalDateTime now = LocalDateTime.now();
		if (startTime.isBefore(now) || !startTime.toLocalDate().equals(now.toLocalDate())) {
			throw new BusinessException(ReservationErrorCode.INVALID_START_TIME);
		}
	}

	@Transactional
	public void cancel(Long memberId, Long reservationId) {
		Reservation reservation = reservationRepository.findById(reservationId)
			.orElseThrow(() -> new BusinessException(ReservationErrorCode.RESERVATION_NOT_FOUND));

		if (!reservation.isOwnedBy(memberId)) {
			throw new BusinessException(ReservationErrorCode.NOT_RESERVATION_OWNER);
		}
		if (!reservation.isCancelled()) {
			throw new BusinessException(ReservationErrorCode.ALREADY_CANCELLED);
		}
		if (!reservation.isEnded(LocalDateTime.now())) {
			throw new BusinessException(ReservationErrorCode.ALREADY_ENDED);
		}
		reservation.cancel();
	}

	@Transactional(readOnly = true)
	public List<ReservationResponse> findMyReservations(Long memberId) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
		LocalDateTime endOfDay = startOfDay.plusDays(1);
		List<Reservation> todayReservations = reservationRepository.findTodayReservations(memberId, startOfDay,
			endOfDay);
		return toResponse(todayReservations, now);
	}

	@Transactional(readOnly = true)
	public List<ReservationResponse> findMyReservationsHistory(Long memberId) {
		LocalDateTime now = LocalDateTime.now();
		List<Reservation> allReservations = reservationRepository.findAllReservations(memberId);
		return toResponse(allReservations, now);
	}

	private List<ReservationResponse> toResponse(List<Reservation> reservations, LocalDateTime now) {
		List<Long> seatIds = reservations
			.stream()
			.map(Reservation::getSeatId)
			.distinct()
			.toList();
		Map<Long, CafeInfo> cafeInfo = cafeInfoPort.getCafeInfo(seatIds);
		return reservations
			.stream()
			.map(r -> ReservationResponse.of(r, cafeInfo.get(r.getSeatId()), now))
			.toList();
	}
}

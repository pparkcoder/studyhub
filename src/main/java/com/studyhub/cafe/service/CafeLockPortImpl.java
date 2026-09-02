package com.studyhub.cafe.service;

import org.springframework.stereotype.Component;

import com.studyhub.cafe.repository.CafeRepository;
import com.studyhub.common.exception.BusinessException;
import com.studyhub.common.exception.CafeErrorCode;
import com.studyhub.reservation.port.CafeLockPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CafeLockPortImpl implements CafeLockPort {

	private final CafeRepository cafeRepository;

	@Override
	public void lock(Long cafeId) {
		cafeRepository.findByIdWithPessimisticLock(cafeId)
			.orElseThrow(() -> new BusinessException(CafeErrorCode.CAFE_NOT_FOUND));
	}
}

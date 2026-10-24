package com.studyhub.cafe.adapter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.studyhub.ai.search.port.CafeRegionPort;
import com.studyhub.cafe.domain.CafeStatus;
import com.studyhub.cafe.repository.CafeRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CafeRegionPortImpl implements CafeRegionPort {

	private final CafeRepository cafeRepository;

	@Override
	public List<String> findActiveRegions() {
		return cafeRepository.findDistinctRegionByStatus(CafeStatus.ACTIVE);
	}
}

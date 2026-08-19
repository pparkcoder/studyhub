package com.studyhub.cafe.repository;

import java.util.List;

import com.studyhub.cafe.domain.Cafe;
import com.studyhub.cafe.dto.request.CafeSearchRequest;

public interface CafeRepositoryCustom {
	List<Cafe> search(CafeSearchRequest request);
}

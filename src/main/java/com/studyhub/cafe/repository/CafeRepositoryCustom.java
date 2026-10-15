package com.studyhub.cafe.repository;

import java.util.List;

import com.studyhub.cafe.domain.Cafe;

public interface CafeRepositoryCustom {
	List<Cafe> search(String region, String keyword);
}

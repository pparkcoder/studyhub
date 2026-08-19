package com.studyhub.cafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.studyhub.cafe.domain.Cafe;

public interface CafeRepository extends JpaRepository<Cafe, Long>, CafeRepositoryCustom {
}

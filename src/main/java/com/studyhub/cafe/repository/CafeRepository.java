package com.studyhub.cafe.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.studyhub.cafe.domain.Cafe;
import com.studyhub.cafe.domain.CafeStatus;

import jakarta.persistence.LockModeType;

public interface CafeRepository extends JpaRepository<Cafe, Long>, CafeRepositoryCustom {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select c from Cafe c where c.id = :cafeId")
	Optional<Cafe> findByIdWithPessimisticLock(Long cafeId);

	List<Cafe> findAllByIdAndStatus(Long id, CafeStatus status);

}

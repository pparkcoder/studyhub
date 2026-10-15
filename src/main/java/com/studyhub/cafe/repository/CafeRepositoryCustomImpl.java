package com.studyhub.cafe.repository;

import static com.studyhub.cafe.domain.QCafe.*;

import java.util.List;

import org.springframework.util.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.studyhub.cafe.domain.Cafe;
import com.studyhub.cafe.domain.CafeStatus;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CafeRepositoryCustomImpl implements CafeRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<Cafe> search(String region, String keyword) {
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(cafe.status.eq(CafeStatus.ACTIVE));
		builder.and(StringUtils.hasText(region) ? cafe.region.eq(region) : null);
		builder.and(StringUtils.hasText(keyword) ? cafe.name.contains(keyword) : null);

		return queryFactory
			.selectFrom(cafe)
			.where(builder)
			.orderBy(cafe.createdDate.desc())
			.fetch();

	}
}

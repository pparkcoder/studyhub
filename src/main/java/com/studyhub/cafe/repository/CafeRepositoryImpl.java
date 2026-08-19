package com.studyhub.cafe.repository;

import static com.studyhub.cafe.domain.QCafe.*;

import java.time.LocalTime;
import java.util.List;

import org.springframework.util.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.studyhub.cafe.domain.Cafe;
import com.studyhub.cafe.dto.request.CafeSearchRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CafeRepositoryImpl implements CafeRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<Cafe> search(CafeSearchRequest request) {
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(StringUtils.hasText(request.getRegion()) ? cafe.region.eq(request.getRegion()) : null);
		builder.and(StringUtils.hasText(request.getKeyword()) ? cafe.name.contains(request.getKeyword()) : null);

		if (Boolean.TRUE.equals(request.getOpenNow())) {
			LocalTime now = LocalTime.now();
			builder.and(cafe.openTime.loe(now).and(cafe.closeTime.goe(now)));
		}

		return queryFactory
			.selectFrom(cafe)
			.where(builder)
			.orderBy(cafe.createdDate.desc())
			.fetch();

	}
}

package com.studyhub;

import static com.tngtech.archunit.base.DescribedPredicate.*;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.*;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "com.studyhub")
public class ModuleDependencyTest {

	@ArchTest
	static final ArchRule reservation은_cafe를_참조하지_않는다 =
		noClasses()
			.that().resideInAPackage("..reservation..")
			.should().dependOnClassesThat().resideInAPackage("..cafe..");

	@ArchTest
	static final ArchRule reservation은_member의_port만_참조한다 =
		noClasses()
			.that().resideInAPackage("..reservation..")
			.should().dependOnClassesThat(
				resideInAPackage("..member..")
					.and(not(resideInAPackage("..member.port..")))
			);

	@ArchTest
	static final ArchRule cafe는_reservation의_port만_참조한다 =
		noClasses()
			.that().resideInAPackage("..cafe..")
			.should().dependOnClassesThat(
				resideInAPackage("..reservation..")
					.and(not(resideInAPackage("..reservation.port..")))
			);

	@ArchTest
	static final ArchRule member는_reservation의_port만_참조한다 =
		noClasses()
			.that().resideInAPackage("..member..")
			.should().dependOnClassesThat(
				resideInAPackage("..reservation..")
					.and(not(resideInAPackage("..reservation.port.."))));

	@ArchTest
	static final ArchRule member는_cafe의_port만_참조한다 =
		noClasses()
			.that().resideInAPackage("..member..")
			.should().dependOnClassesThat(
				resideInAPackage("..cafe..")
					.and(not(resideInAPackage("..cafe.port.."))));

	@ArchTest
	static final ArchRule common은_어느_도메인모듈도_참조하지_않는다 =
		noClasses()
			.that().resideInAPackage("..common..")
			.should().dependOnClassesThat(
				resideInAPackage("..cafe..")
					.or(resideInAPackage("..reservation.."))
					.or(resideInAPackage("..member.."))
					.or(resideInAPackage("..ai.search..")));

	@ArchTest
	static final ArchRule ai_search는_다른_도메인모듈을_참조하지_않는다 =
		noClasses()
			.that().resideInAPackage("..ai.search..")
			.should().dependOnClassesThat(
				resideInAPackage("..cafe..")
					.or(resideInAPackage("..reservation.."))
					.or(resideInAPackage("..member..")));

	@ArchTest
	static final ArchRule cafe는_ai_search의_port만_참조한다 =
		noClasses()
			.that().resideInAPackage("..cafe..")
			.should().dependOnClassesThat(
				resideInAPackage("..ai.search..")
					.and(not(resideInAPackage("..ai.search.port.."))));

	@ArchTest
	static final ArchRule member와_reservation은_ai_search를_참조하지_않는다 =
		noClasses()
			.that().resideInAnyPackage("..member..", "..reservation..")
			.should().dependOnClassesThat().resideInAPackage("..ai.search..");

	@ArchTest
	static final ArchRule AI_라이브러리는_ai_search_adapter에서만_사용한다 =
		noClasses()
			.that().resideOutsideOfPackage("com.studyhub.ai.search.adapter..")
			.should().dependOnClassesThat().resideInAnyPackage("org.springframework.ai..", "com.anthropic..");

	@ArchTest
	static final ArchRule 카카오_응답_형식은_cafe_adapter에서만_사용한다 =
		noClasses()
			.that().resideOutsideOfPackage("com.studyhub.cafe.adapter..")
			.should().dependOnClassesThat().resideInAnyPackage("com.studyhub.cafe.adapter.kakao..");
}

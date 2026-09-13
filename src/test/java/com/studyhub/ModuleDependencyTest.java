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
	static final ArchRule reservation은_cafe와_member를_참조하지_않는다 =
		noClasses()
			.that().resideInAPackage("..reservation..")
			.should().dependOnClassesThat(
				resideInAPackage("..cafe..")
					.or(resideInAPackage("..member..")));

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
					.or(resideInAPackage("..member..")));
}

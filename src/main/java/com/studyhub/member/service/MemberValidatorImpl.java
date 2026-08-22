package com.studyhub.member.service;

import org.springframework.stereotype.Component;

import com.studyhub.member.domain.Member;
import com.studyhub.member.domain.MemberStatus;
import com.studyhub.member.repository.MemberRepository;
import com.studyhub.reservation.port.MemberValidationResult;
import com.studyhub.reservation.port.MemberValidator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberValidatorImpl implements MemberValidator {

	private final MemberRepository memberRepository;

	@Override
	public MemberValidationResult validate(Long memberId) {
		Member member = memberRepository.findById(memberId).orElse(null);

		if (member == null) {
			return MemberValidationResult.NOT_FOUND;
		}
		if (member.getStatus() == MemberStatus.WITHDRAWN) {
			return MemberValidationResult.WITHDRAWN;
		}
		return MemberValidationResult.VALID;
	}
}

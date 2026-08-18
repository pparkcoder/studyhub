package com.studyhub.member.service;

import org.springframework.stereotype.Component;

import com.studyhub.cafe.port.OwnerValidationResult;
import com.studyhub.cafe.port.OwnerValidator;
import com.studyhub.member.domain.Member;
import com.studyhub.member.domain.MemberStatus;
import com.studyhub.member.domain.Role;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberOwnerValidator implements OwnerValidator {

	private final MemberService memberService;

	@Override
	public OwnerValidationResult validate(Long memberId) {
		Member member = memberService.findById(memberId).orElse(null);

		if (member == null) {
			return OwnerValidationResult.NOT_FOUND;
		}
		if (member.getStatus() == MemberStatus.WITHDRAWN) {
			return OwnerValidationResult.WITHDRAWN;
		}
		if (member.getRole() != Role.OWNER) {
			return OwnerValidationResult.NOT_OWNER_ROLE;
		}
		return OwnerValidationResult.VALID;
	}
}

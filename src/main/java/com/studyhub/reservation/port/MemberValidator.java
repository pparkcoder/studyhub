package com.studyhub.reservation.port;

public interface MemberValidator {

	MemberValidationResult validate(Long memberId);
}

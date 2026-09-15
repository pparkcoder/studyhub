package com.studyhub.member.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studyhub.common.exception.BusinessException;
import com.studyhub.common.exception.MemberErrorCode;
import com.studyhub.member.domain.Member;
import com.studyhub.member.dto.request.SignUpRequest;
import com.studyhub.member.dto.request.WithdrawRequest;
import com.studyhub.member.dto.response.SignUpResponse;
import com.studyhub.member.port.ReservationQueryPort;
import com.studyhub.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final ReservationQueryPort reservationQueryPort;

	@Transactional(readOnly = true)
	public Optional<Member> findByUsername(String username) {
		return memberRepository.findByUsername(username);
	}

	@Transactional(readOnly = true)
	public Optional<Member> findById(Long id) {
		return memberRepository.findById(id);
	}

	@Transactional
	public SignUpResponse signup(SignUpRequest signUpRequest) {
		if (memberRepository.findByUsername(signUpRequest.getUsername()).isPresent()) {
			throw new BusinessException(MemberErrorCode.DUPLICATE_USERNAME);
		}
		String password = passwordEncoder.encode(signUpRequest.getPassword());
		Member member = Member.from(signUpRequest, password);
		Member saveMember = memberRepository.save(member);
		return SignUpResponse.from(saveMember);
	}

	@Transactional
	public void withdraw(Long memberId, WithdrawRequest request) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));

		if (member.isWithdrawn()) {
			throw new BusinessException(MemberErrorCode.ALREADY_WITHDRAWN);
		}

		boolean passwordMatches = passwordEncoder.matches(request.getPassword(), member.getPassword());
		if (!passwordMatches) {
			throw new BusinessException(MemberErrorCode.PASSWORD_MISMATCH);
		}

		if (reservationQueryPort.hasActiveReservation(memberId)) {
			throw new BusinessException(MemberErrorCode.ACTIVE_RESERVATION_EXISTS);
		}

		member.withdraw();
		member.clearRefreshToken();
	}
}

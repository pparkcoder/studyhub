package com.studyhub.member.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.studyhub.common.exception.BusinessException;
import com.studyhub.common.exception.MemberErrorCode;
import com.studyhub.member.domain.Member;
import com.studyhub.member.dto.request.SignUpRequest;
import com.studyhub.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	public Optional<Member> findByUsername(String username) {
		return memberRepository.findByUsername(username);
	}

	public Optional<Member> findById(Long id) {
		return memberRepository.findById(id);
	}

	public Long signup(SignUpRequest signUpRequest) {
		if (memberRepository.findByUsername(signUpRequest.getUsername()).isPresent()) {
			throw new BusinessException(MemberErrorCode.DUPLICATE_USERNAME);
		}
		String password = passwordEncoder.encode(signUpRequest.getPassword());
		Member member = Member.from(signUpRequest, password);
		Member saveMember = memberRepository.save(member);
		return saveMember.getId();
	}
}

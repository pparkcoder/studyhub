package com.studyhub.member.domain;

import java.time.LocalDateTime;

import com.studyhub.common.entity.BaseTimeEntity;
import com.studyhub.member.dto.request.SignUpRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 20, nullable = false, unique = true)
	private String username;

	@Column(length = 20, nullable = false)
	private String nickname;

	@Column(length = 50, nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MemberStatus status;

	@Column(name = "refresh_token_hash")
	private String refreshTokenHash;

	@Column(name = "refresh_token_expired_at")
	private LocalDateTime refreshTokenExpiredAt;

	@Builder(access = AccessLevel.PRIVATE)
	private Member(String username, String nickname, String email, String password, Role role) {
		this.username = username;
		this.nickname = nickname;
		this.email = email;
		this.password = password;
		this.role = role;
		this.status = MemberStatus.ACTIVE;
	}

	public static Member from(SignUpRequest request, String password) {
		return Member.builder()
			.username(request.getUsername())
			.nickname(request.getNickname())
			.email(request.getEmail())
			.password(password)
			.role(request.getRole())
			.build();
	}

	public void updateRefreshToken(String refreshTokenHash, LocalDateTime expiredAt) {
		this.refreshTokenHash = refreshTokenHash;
		this.refreshTokenExpiredAt = expiredAt;
	}

	public void clearRefreshToken() {
		this.refreshTokenHash = null;
		this.refreshTokenExpiredAt = null;
	}

	public void withdraw() {
		this.status = MemberStatus.WITHDRAWN;
	}

	public void changePassword(String password) {
		this.password = password;
	}
}

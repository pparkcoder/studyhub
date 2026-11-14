package com.studyhub.cafe.port;

public record ResolvedAddress(
	String sido,
	String sigungu,
	String sigunguCode,
	double latitude,
	double longitude
) {

	public String regionName() {
		if (sigungu == null) {
			return sido;
		}
		return sido + " " + sigungu;
	}
}

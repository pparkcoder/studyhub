package com.studyhub.cafe.adapter;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.studyhub.cafe.adapter.kakao.KakaoAddress;
import com.studyhub.cafe.adapter.kakao.KakaoAddressDocument;
import com.studyhub.cafe.adapter.kakao.KakaoAddressResponse;
import com.studyhub.cafe.port.AddressResolver;
import com.studyhub.cafe.port.ResolvedAddress;
import com.studyhub.common.exception.BusinessException;
import com.studyhub.common.exception.CafeErrorCode;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AddressResolverImpl implements AddressResolver {

	private static final String KAKAO_ADDRESS_API_PATH = "/v2/local/search/address.json";
	private static final Set<String> ALLOWED_ADDRESS_TYPES = Set.of("REGION_ADDR", "ROAD_ADDR");
	private final RestClient restClient;

	public AddressResolverImpl(
		RestClient.Builder builder,
		@Value("${kakao.rest-api-key}") String kakaoRestApiKey) {
		HttpClient httpClient = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(2))
			.build();
		JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
		requestFactory.setReadTimeout(Duration.ofSeconds(3));

		this.restClient = builder
			.baseUrl("https://dapi.kakao.com")
			.defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoRestApiKey)
			.requestFactory(requestFactory)
			.build();
	}

	@Override
	public Optional<ResolvedAddress> resolve(String address) {
		KakaoAddressResponse response = search(address);
		if (response == null || response.documents() == null || response.documents().isEmpty()) {
			return Optional.empty();
		}

		KakaoAddressDocument kakaoAddressDocument = response.documents().get(0);
		if (!ALLOWED_ADDRESS_TYPES.contains(kakaoAddressDocument.addressType())) {
			return Optional.empty();
		}
		if (kakaoAddressDocument.address() == null) {
			log.warn("주소 검색 결과에 지번 주소 정보가 없습니다. address={}", address);
			return Optional.empty();
		}
		return Optional.of(toResolvedAddress(kakaoAddressDocument));
	}

	private KakaoAddressResponse search(String address) {
		try {
			return restClient.get()
				.uri(uriBuilder -> uriBuilder
					.path(KAKAO_ADDRESS_API_PATH)
					.queryParam("query", address)
					.build())
				.retrieve()
				.body(KakaoAddressResponse.class);
		} catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.Forbidden e) {
			log.error("카카오 API 인증 실패. REST API 키와 앱 설정을 확인해야 합니다.", e);
			throw new BusinessException(CafeErrorCode.ADDRESS_SEARCH_UNAVAILABLE);
		} catch (RestClientException e) {
			log.warn("카카오 주소 검색 실패. address={}", address, e);
			throw new BusinessException(CafeErrorCode.ADDRESS_SEARCH_UNAVAILABLE);
		}
	}

	private ResolvedAddress toResolvedAddress(KakaoAddressDocument kakaoAddressDocument) {
		KakaoAddress address = kakaoAddressDocument.address();
		String sigungu = StringUtils.hasText(address.region2depthName()) ? address.region2depthName() : null;

		return new ResolvedAddress(
			address.region1depthName(),
			sigungu,
			address.bCode().substring(0, 5),
			Double.parseDouble(kakaoAddressDocument.y()),
			Double.parseDouble(kakaoAddressDocument.x())
		);
	}
}

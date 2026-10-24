package com.studyhub.ai.search.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studyhub.ai.search.dto.request.AiSearchRequest;
import com.studyhub.ai.search.dto.response.AiSearchResponse;
import com.studyhub.ai.search.service.AiSearchService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ai-search")
public class AiSearchController {

	private final AiSearchService aiSearchService;

	@PostMapping
	public AiSearchResponse search(@RequestBody @Valid AiSearchRequest aiSearchRequest) {
		return aiSearchService.search(aiSearchRequest);
	}
}

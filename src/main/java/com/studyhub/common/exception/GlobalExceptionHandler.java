package com.studyhub.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
		ErrorCode errorCode = e.getErrorCode();
		return ResponseEntity.status(errorCode.getStatus()).body(ErrorResponse.of(errorCode));
	}

	// @Valid 검증 실패
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		String message = e.getBindingResult().getFieldErrors()
			.stream()
			.findFirst()
			.map(FieldError::getDefaultMessage)
			.orElse("잘못된 요청입니다.");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("VALIDATION_ERROR", message));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception e) {
		log.error(e.getMessage(), e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(new ErrorResponse("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."));
	}

}

package com.junwoo.hamkke.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 12.
 */
@Getter
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 내부 에러 발생"),
    UNSUCCESSFUL_AUTHENTICATION(HttpStatus.UNAUTHORIZED.value(), "이메일 또는 비밀번호가 일치하지 않습니다"),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 Refresh Token입니다"),
    CANNOT_FOUND_USER(HttpStatus.BAD_REQUEST.value(), "사용자를 찾을 수 없습니다"),

    ALREADY_EXISTS_NICKNAME(HttpStatus.CONFLICT.value(), "이미 존재하는 닉네임입니다"),
    ALREADY_EXISTS_EMAIL(HttpStatus.CONFLICT.value(), "이미 존재하는 이메일입니다"),

    EMPTY_FILE(HttpStatus.BAD_REQUEST.value(), "업로드할 파일이 없습니다"),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST.value(), "파일 크기는 10MB를 초과할 수 없습니다"),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST.value(), "이미지 파일만 업로드 가능합니다"),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST.value(), "유효하지 않은 파일 확장자입니다"),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "파일 업로드에 실패했습니다"),
    FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "파일 삭제에 실패했습니다"),

    CANNOT_FOUND_GOAL(HttpStatus.NOT_FOUND.value(), "해당 스터디룸에서 작성한 목표를 찾지 못했습니다"),

    CANNOT_FOUND_ROOM(HttpStatus.NOT_FOUND.value(), "스터디룸을 찾을 수 없습니다"),
    SECRET_ROOM_NEED_PASSWORD(HttpStatus.BAD_REQUEST.value(), "비밀방은 비밀번호 입력이 필수입니다");
    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}

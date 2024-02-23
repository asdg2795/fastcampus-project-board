package com.fastcampus.projectboard.dto.response;

import com.fastcampus.projectboard.dto.ArticleDto;

import java.time.LocalDateTime;

public record ArticleResponse(
        Long id,
        String title,
        String content,
        String hashtag,
        LocalDateTime createdAt,
        String email,
        String nickname
){

    public static ArticleResponse of(Long id, String title, String content, String hashtag, LocalDateTime createdAt, String email, String nickname) {
        return new ArticleResponse(id, title, content, hashtag, createdAt, email, nickname);
    }
    // 이 메서드는 주어진 값으로 새로운 `ArticleResponse` 객체를 생성
    // 이러한 정적 팩토리 메서드를 제공함으로써 객체 생성 시 추가적인 유효성 검사나 초기화를 수행

    public static ArticleResponse from(ArticleDto dto) {
        String nickname = dto.userAccountDto().nickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.userAccountDto().userId();
        }
        // 이 메서드는 `ArticleDto` 객체를 `ArticleResponse` 객체로 변환
        // 이 변환은 게시글을 데이터 전송 객체(DTO)에서 응답객체로 변환하는 역할을 수행한다.
        // 이 메서드는 `ArticleDto` 객체에서 필요한 정보를 추출하여
        // 새로운 `ArticleResponse` 객체를 생성하여 반환한다.
        // 만약 작성자의 닉네임이 없으면 사용자 아이디를 대신 사용합니다.

        return new ArticleResponse(
                dto.id(),
                dto.title(),
                dto.content(),
                dto.hashtag(),
                dto.createdAt(),
                dto.userAccountDto().email(),
                nickname
        );
    }

}
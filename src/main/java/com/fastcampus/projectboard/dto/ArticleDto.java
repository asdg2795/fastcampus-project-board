package com.fastcampus.projectboard.dto;

import com.fastcampus.projectboard.domain.Article;
import com.fastcampus.projectboard.domain.UserAccount;

import java.time.LocalDateTime;

public record ArticleDto(
        Long id,
        UserAccountDto userAccountDto,
        String title,
        String content,
        String hashtag,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {
    public static ArticleDto of(UserAccountDto userAccountDto, String title, String content, String hashtag) {
        return new ArticleDto(null, userAccountDto, title, content, hashtag, null, null, null, null);
    }
    // 이 메서드는 주로 새로운 기사를 생성하는 경우에 사용될 수 있다.
    // 새로운 기사를 생성할 때는 아직 기사에 대한 정보가 데이터베이스에 저장되지 않았으므로,
    // ID와 날짜 및 작성자 정보는 모두 null로 설정

    public static ArticleDto of(Long id, UserAccountDto userAccountDto, String title, String content, String hashtag, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ArticleDto(id, userAccountDto, title, content, hashtag, createdAt, createdBy, modifiedAt, modifiedBy);
    }
    // 이 코드는 주어진 값들을 사용하여 새로운 `ArticleDto` 객체를 생성
    // 이 메서드는 주로 데이터베이스에서 기존의 기사를 가져올 때 사용
    // 각 필드에 해당하는 값을 인자로 받아 새로운 `ArticleDto` 객체를 생성

    public static ArticleDto from(Article entity) {
        return new ArticleDto(
                // `ArticleDto.from(Article entity)` : 이 메서드는 주어진 `Article` 엔티티 단체에서 `ArticleDto` 객체로 변환합니다.
                // 이 변환은 엔티티 객체에서 DTO 객체로의 변환을 수행
                entity.getId(),
                UserAccountDto.from(entity.getUserAccount()),
                // 이 부분은 `entity` 에서 사용자 계정 정보를 추출하여 `UserAccountDto.from()` 메서드를 사용하여 해당 정보를
                // `UserAccountDto` 객체로 변환
                // 이렇게 함으로써 사용자 계정 정보를 `ArticleDto` 개체에 포함시킬 수 있다.
                entity.getTitle(),
                entity.getContent(),
                entity.getHashtag(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }


    // `ArticleDto` 객체를 `Article` 엔티티 객체로 변환하는 메서드를 정의
    // 이 메서드는 주어진 사용자 계정 정보와 `ArticleDto` 객체의 데이터를 사용하여 새로운 `Article` 엔티티 객체를 생성
    public Article toEntity(UserAccount userAccount) {
        // 이 메서드는 `ArticleDto` 객체를 `Article` 엔티티 객체로 변환
        // 이 변환은 DTO 객체에서 엔티티 객체로의 변환을 수행
        return Article.of(
                userAccount,
                title,
                content,
                hashtag
        );
        // 이 부분은 `Article` 클래스에 정의된 정적 팩토리 메서드 `of()`를 사용하여 새로운 `Article` 엔티티 객체를 생성

    }
}
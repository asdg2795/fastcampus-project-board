package com.fastcampus.projectboard.dto.request;

import com.fastcampus.projectboard.dto.ArticleDto;
import com.fastcampus.projectboard.dto.UserAccountDto;

public record ArticleRequest(
        String title,
        String content,
        String hashtag
) {

    public static ArticleRequest of(String title, String content, String hashtag) {
        return new ArticleRequest(title, content, hashtag);
    }
    // 이 메서드는 주어진 값으로 새로운  `ArticleRequest` 객체를 생성합니다.
    // 이러한 정적 팩토리 메서드를 제공함으로써 객체 생성 시 추가적인 유효성 검사나 초기화를 수행할 수 있습니다.

    public ArticleDto toDto(UserAccountDto userAccountDto) {
        return ArticleDto.of(
                userAccountDto,
                title,
                content,
                hashtag
        );
    }
    // 이 메서드는 `ArticleRequest` 객체를 `ArticleDto` 객체로 변환한다.
    // 이 변환은 게시글을 데이터 전송 객체(DTO)로 변환하는 역할을 수행
    // 이 메서도는 사용자 계정 정보(`UserAccountDto`)와 함께 게시글의 제목, 내용, 해시태그를 가지고 있는 `ArticleDto` 객체를 생성하여 반환

    // `ArticleRequest` 레코드는 게시글을 생성하는 데 필요한 데이터를 포함하며,
    // 변환 메서드를 통해 다른 형식의 객체로 변환할 수 있습니다.
    // 또한 레코드의 불변성은 객체의 안전성과 코드의 가독성을 향상시킵니다.
}
package com.fastcampus.projectboard.domain.constant;

import lombok.Getter;

public enum SearchType {
    TITLE("제목"),
    CONTENT("본문"),
    ID("유저 ID"),
    NICKNAME("닉네임"),
    HASHTAG("해시태그");

    @Getter private final String description;

    SearchType(String description) {
        this.description = description;
    }


    // 이렇게 함으로써 각 검색 유형에 대한 설명을 가지고 있는 열거형을 정의할 수 있습니다.
    // 이를 통해 검색 옵션을 사용자에게 보여줄 때 각 옵션에 대한 설명을 함께 제공할 수 있습니다.
    }
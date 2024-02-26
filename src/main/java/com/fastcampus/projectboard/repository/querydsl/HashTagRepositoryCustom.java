package com.fastcampus.projectboard.repository.querydsl;

import java.util.List;

public interface HashTagRepositoryCustom {
    List<String> findAllHashtagNames();
}
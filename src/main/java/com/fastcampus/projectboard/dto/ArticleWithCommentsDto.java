package com.fastcampus.projectboard.dto;

import com.fastcampus.projectboard.domain.Article;
import com.fastcampus.projectboard.domain.ArticleComment;
import com.fastcampus.projectboard.dto.ArticleWithCommentsDto;
import com.fastcampus.projectboard.domain.UserAccount;

import java.time.LocalDateTime;

public record ArticleWithCommentsDto(
        Long id,
        Long articleId,
        UserAccountDto userAccountDto,
        Long parentCommentId,
        String content,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

    public static ArticleWithCommentsDto of(Long articleId, UserAccountDto userAccountDto, String content) {
        return ArticleWithCommentsDto.of(articleId, userAccountDto, null, content);
    }

    public static ArticleWithCommentsDto of(Long articleId, UserAccountDto userAccountDto, Long parentCommentId, String content) {
        return ArticleWithCommentsDto.of(null, articleId, userAccountDto, parentCommentId, content, null, null, null, null);
    }

    public static ArticleWithCommentsDto of(Long id, Long articleId, UserAccountDto userAccountDto, Long parentCommentId, String content, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ArticleWithCommentsDto(id, articleId, userAccountDto, parentCommentId, content, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static ArticleWithCommentsDto from(ArticleComment entity) {
        return new ArticleWithCommentsDto(
                entity.getId(),
                entity.getArticle().getId(),
                UserAccountDto.from(entity.getUserAccount()),
                entity.getParentCommentId(),
                entity.getContent(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    public ArticleComment toEntity(Article article, UserAccount userAccount) {
        return ArticleComment.of(
                article,
                userAccount,
                content
        );
    }

}
package com.fastcampus.projectboard.dto;

import com.fastcampus.projectboard.domain.Article;
import com.fastcampus.projectboard.domain.ArticleComment;
import com.fastcampus.projectboard.domain.UserAccount;

import java.time.LocalDateTime;

/**
 * DTO for {@link com.fastcampus.projectboard.domain.ArticleComment}
 */
public record ArticleCommentDto(
        Long id,
        Long articleId,
        UserAccountDto userAccountDto, // user ID 만 가지면 되는거 아닌가? 왜 DTO 를 통채로 가지고 있어야 하지??
        Long parentCommentId,
        String content,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {
    public static ArticleCommentDto of(
            Long articleId,
            UserAccountDto userAccountDto,
            String content
    ) {
        return ArticleCommentDto.of(null, articleId, userAccountDto, null, content, null, null, null, null);
    }

    public static ArticleCommentDto of(
            Long articleId,
            UserAccountDto userAccountDto,
            Long parentCommentId,
            String content
    ) {
        return ArticleCommentDto.of(null, articleId, userAccountDto, parentCommentId, content, null, null, null, null);
    }

    public static ArticleCommentDto of(
            Long id,
            Long articleId,
            UserAccountDto userAccountDto,
            Long parentCommentId,
            String content,
            LocalDateTime createdAt,
            String createdBy,
            LocalDateTime modifiedAt,
            String modifiedBy) {
        return new ArticleCommentDto(id, articleId, userAccountDto, parentCommentId, content, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static ArticleCommentDto from(ArticleComment entity) {
        return new ArticleCommentDto(
                entity.getId(),
                entity.getArticle().getId(),
                UserAccountDto.from(entity.getUserAccount()), // ???
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


package com.fastcampus.projectboard.dto;

import java.io.Serializable;

/**
 * DTO for {@link com.fastcampus.projectboard.domain.ArticleComment}
 */
public record ArticleCommentUpdateDto(String content) implements Serializable {
    public static ArticleCommentUpdateDto of(String content) {
        return new ArticleCommentUpdateDto(content);
    }
}


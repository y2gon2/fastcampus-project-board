package com.fastcampus.projectboard.dto;

import com.fastcampus.projectboard.domain.Article;
import com.fastcampus.projectboard.domain.Hashtag;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

// 동일 hashtag 를 가진 게시글을 모아서 보고자 할 때 (optional)
public record HashtagWithArticlesDto(
        Long id,
        Set<ArticleDto> articleDtos,
        String hashtagName,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {
    public static HashtagWithArticlesDto of(
            Long id,
            Set<ArticleDto> articleDtos,
            String hashtagName,
            LocalDateTime createdAt,
            String createdBy,
            LocalDateTime modifiedAt,
            String modifiedBy
    ) {
        return new HashtagWithArticlesDto(
                id,
                articleDtos,
                hashtagName,
                createdAt,
                createdBy,
                modifiedAt,
                modifiedBy);
    }

    public static HashtagWithArticlesDto of(
            Set<ArticleDto> articleDtos,
            String hashtagName
    ) {
        return new HashtagWithArticlesDto(
                null,
                articleDtos,
                hashtagName,
                null,
                null,
                null,
                null
        );
    }

    public static HashtagWithArticlesDto from(Hashtag entity) {
        return new HashtagWithArticlesDto(
                entity.getId(),
                entity.getArticles().stream()
                    .map(ArticleDto::from)
                    .collect(Collectors.toUnmodifiableSet()),
                entity.getHashtagName(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    public Hashtag toEntity() {
        return Hashtag.of(hashtagName);
    }
}

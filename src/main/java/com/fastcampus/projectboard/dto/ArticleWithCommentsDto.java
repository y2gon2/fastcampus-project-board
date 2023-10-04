package com.fastcampus.projectboard.dto;

import com.fastcampus.projectboard.domain.Article;
import com.fastcampus.projectboard.domain.Hashtag;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.LinkedHashSet;

/**
 * DTO for {@link com.fastcampus.projectboard.domain.Article}
 */
public record ArticleWithCommentsDto(
        Long id,
        UserAccountDto userAccountDto,
        Set<ArticleCommentDto> articleCommentDtos,
        String title,
        String content,
        Set<HashtagDto> hashtagDtos,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
        ) {
    public static ArticleWithCommentsDto of(Long id,
                                  UserAccountDto userAccountDto,
                                  Set<ArticleCommentDto> articleCommentDtos,
                                  String title,
                                  String content,
                                  Set<HashtagDto> hashtagDtos,
                                  LocalDateTime createdAt,
                                  String createdBy,
                                  LocalDateTime modifiedAt,
                                  String modifiedBy) {
        return new ArticleWithCommentsDto(id,
                userAccountDto,
                articleCommentDtos,
                title,
                content,
                hashtagDtos,
                createdAt,
                createdBy,
                modifiedAt,
                modifiedBy
        );
    }

    public static ArticleWithCommentsDto from(Article entity) {
        return new ArticleWithCommentsDto(
                entity.getId(),
                UserAccountDto.from(entity.getUserAccount()),
                entity.getArticleComments().stream()
                        .map(ArticleCommentDto::from)
                        .collect(Collectors.toCollection(LinkedHashSet::new)),
                entity.getTitle(),
                entity.getContent(),
                entity.getHashtags().stream()
                        .map(HashtagDto::from)
                        .collect(Collectors.toUnmodifiableSet()),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }
}


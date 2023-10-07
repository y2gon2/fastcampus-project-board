package com.fastcampus.projectboard.dto.response;

import com.fastcampus.projectboard.dto.ArticleCommentDto;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 * DTO for {@link com.fastcampus.projectboard.domain.ArticleComment}
 */
public record ArticleCommentResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        String email,
        String nickname,
        String userId,
        Long parentCommentId,
        Set<ArticleCommentResponse> childComments
) {
    public static ArticleCommentResponse of(
            Long id,
            String content,
            LocalDateTime createdAt,
            String email,
            String nickname,
            String userId
    ) {
        return ArticleCommentResponse.of(
                id,
                content,
                createdAt,
                email,
                nickname,
                userId,
                null
        );
    }

    public static ArticleCommentResponse of(
            Long id,
            String content,
            LocalDateTime createdAt,
            String email,
            String nickname,
            String userId,
            Long parentCommentId
    ) {
        // view 에서 작성일 순으로 보여주기 위해 정렬 가능 집합 (TreeSet) 을 사용
        // 해당 정렬을 위해 Comparator 를 사용
        // 2차 정렬 조건으로 id 를 추가함.
        Comparator<ArticleCommentResponse> childCommentComparator = Comparator
                .comparing(ArticleCommentResponse::createdAt)
                .thenComparingLong(ArticleCommentResponse::id);
        return new ArticleCommentResponse(
                id,
                content,
                createdAt,
                email,
                nickname,
                userId,
                parentCommentId,
                new TreeSet<>(childCommentComparator)
        );
    }

    public static ArticleCommentResponse from(ArticleCommentDto dto) {
        String nickname = dto.userAccountDto().nickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.userAccountDto().userId();
        }

        return ArticleCommentResponse.of(
                dto.id(),
                dto.content(),
                dto.createdAt(),
                dto.userAccountDto().email(),
                nickname,
                dto.userAccountDto().userId(),
                dto.parentCommentId()
        );
    }

    public boolean hasParentComment() {
        return parentCommentId != null;
    }
}
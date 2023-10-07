package com.fastcampus.projectboard.dto.response;

import com.fastcampus.projectboard.dto.ArticleCommentDto;
import com.fastcampus.projectboard.dto.ArticleWithCommentsDto;
import com.fastcampus.projectboard.dto.HashtagDto;
import com.fastcampus.projectboard.dto.request.ArticleCommentRequest;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * DTO for {@link com.fastcampus.projectboard.domain.Article}
 */
public record ArticleWithCommentsResponse(
        Long id,
        String title,
        String content,
        Set<String> hashtags,
        LocalDateTime createdAt,
        String email,
        String nickname,
        String userId,
        Set<ArticleCommentResponse> articleCommentsResponse
) {
    public static ArticleWithCommentsResponse of(
            Long id,
            String title,
            String content,
            Set<String> hashtags,
            LocalDateTime createdAt,
            String email,
            String nickname,
            String userId,
            Set<ArticleCommentResponse> articleCommentResponses) {
        return new ArticleWithCommentsResponse(
                id,
                title,
                content,
                hashtags,
                createdAt,
                email,
                nickname,
                userId,
                articleCommentResponses);
    }

    public static ArticleWithCommentsResponse from(ArticleWithCommentsDto dto) {
        String nickname = dto.userAccountDto().nickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.userAccountDto().userId();
        }

        return new ArticleWithCommentsResponse(
                dto.id(),
                dto.title(),
                dto.content(),
                dto.hashtagDtos().stream()
                        .map(HashtagDto::hashtagName)
                        .collect(Collectors.toUnmodifiableSet()),
                dto.createdAt(),
                dto.userAccountDto().email(),
                nickname,
                dto.userAccountDto().userId(),
                organizeChildComments(dto.articleCommentDtos())
        );
    }

    // 현재 logic 은 댓글과 대댓글이 하나의 table 에 포함되어 각 댓글이 parentId 를 찾아가는 방식
    // 따라서 댓글/대댓글 계층이 존재하지 않는다.
    // 그런데 view 에서는 계층이 존재해야 구조적으로 댓글 밑에 대댓글이 오는 형태를 구성할 수 있다.
    // 이런 계층적 mapping 을 잡아주는 logic 을 구현
    //
    // 구현 아이디어
    // 모든 댓글, 대댓글을 순회 하면서,
    // 1. 부모댓글이 null 인 즉 최초 댓글은 set 을 가진다.
    // 2. 대댓글은 부모 댓글 set 애 add 되고 root collection 에서 삭제한다. (2차 대댓글의 경우, 1차 대댓글도 child Set 를 가질 수 있는 동일 구조)
    // 3. 해당 부모 set 에 add 되고 root collection 에서 삭제한다.
    //    (해당 과정에서 댓글 끼리 정렬, 각 대댓글끼리 정렬되어야 하며 그 기준이 다르다.)
    private static Set<ArticleCommentResponse> organizeChildComments(Set<ArticleCommentDto> dtos) {
        // 0. 위 구조를 생성하기 이한 준비로, id 를 통해서 data 에 접근할 수 있도록 Map 을 생성한다.
        Map<Long, ArticleCommentResponse> map = dtos.stream()
                .map(ArticleCommentResponse::from)
                .collect(Collectors.toMap(ArticleCommentResponse::id, Function.identity()));

        // 1. 부모 comment 를 가지는 여부에 따라 구분시킴
        map.values().stream()
                .filter(ArticleCommentResponse::hasParentComment) // 자식 댓글 (대댓글만 찾음)
                .forEach(comment -> {
                    ArticleCommentResponse parentComment = map.get(comment.parentCommentId());
                    parentComment.childComments().add(comment); // 2. 부모 댓글 id 를 key 로 부모 댓글을 찾아서 해당 댓글을 넣어준다.
                });

        // 3. root 댓글만 treeSet 을 생성하여 반환
        return map.values().stream()
                .filter(comment -> !comment.hasParentComment())
                .collect(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator
                                .comparing(ArticleCommentResponse::createdAt)
                                .reversed()
                                .thenComparingLong(ArticleCommentResponse::id)
                        )
                ));

    }

}
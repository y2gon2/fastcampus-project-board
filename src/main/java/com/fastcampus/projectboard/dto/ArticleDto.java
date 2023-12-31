package com.fastcampus.projectboard.dto;

import com.fastcampus.projectboard.domain.Article;
import com.fastcampus.projectboard.domain.Hashtag;
import com.fastcampus.projectboard.domain.UserAccount;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DTO for {@link com.fastcampus.projectboard.domain.Article}
 */
public record ArticleDto(
        Long id,
        UserAccountDto userAccountDto,
        String title,
        String content,
        Set<HashtagDto> hashtagDtos,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

    public static ArticleDto of(
            UserAccountDto userAccountDto,
            String title,
            String content,
            Set<HashtagDto> hashtagDtos
            ) {
        return new ArticleDto(
                null,
                userAccountDto,
                title,
                content,
                hashtagDtos,
                null,
                null,
                null,
                null);
    }

    // record 에서 parameter input constructor 는 내부적으로 자동 생성되어 있음.
    public static ArticleDto of(
            Long id,
            UserAccountDto userAccountDto,
            String title,
            String content,
            Set<HashtagDto> hashtagDtos,
            LocalDateTime createdAt,
            String createdBy,
            LocalDateTime modifiedAt,
            String modifiedBy
    ) {
        return new ArticleDto(
                id,
                userAccountDto,
                title,
                content,
                hashtagDtos,
                createdAt,
                createdBy,
                modifiedAt,
                modifiedBy
        );
    }

    // DTO - Entity mapping methods
    // 해당 logic 의 장점 : Article 은 DTO 의 존재를 몰라도 된다??
    //                    Article 은 그냥 자체적으로 Entity 를 생성하면 된다.
    //                    Service logic 등에서 필요한 경우 DTO 를 생성하여 사용하고
    //                    반대의 경우 아래 toEntity() 로 생성하여 사용할 수 있다.

    // Entity로부터 DTO 생성
    public static ArticleDto from(Article entity) {
        return new ArticleDto(
                entity.getId(),
                UserAccountDto.from(entity.getUserAccount()),
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

    // DTO 로부터 entity 생성
    public Article toEntity(UserAccount userAccount) {
        return Article.of(
                userAccount,
                title,
                content
        );
    }
}


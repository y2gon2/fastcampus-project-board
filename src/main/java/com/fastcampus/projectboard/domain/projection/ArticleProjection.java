package com.fastcampus.projectboard.domain.projection;

import com.fastcampus.projectboard.domain.Article;
import com.fastcampus.projectboard.domain.UserAccount;
import org.springframework.data.rest.core.config.Projection;

import java.time.LocalDateTime;

// Projection (https://docs.spring.io/spring-data/rest/docs/current/reference/html/#projections-excerpts)
// Data REST API 로 응답 하는 정보를 직접 설정하겠다. (JSON 구조를 직접 설정?)
@Projection(name = "withUserAccount", types = Article.class)
public interface ArticleProjection {
    Long getId();
    UserAccount getUserAccount();
    String getTitle();
    String getContent();
    LocalDateTime getCreatedAt();
    String getCreatedBy();
    LocalDateTime getModifiedAt();
    String getModifiedBy();
}

package com.fastcampus.projectboard.repository;

import com.fastcampus.projectboard.domain.Article;
import com.fastcampus.projectboard.domain.QArticle;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ArticleRepository extends
        JpaRepository<Article, Long>,
        QuerydslPredicateExecutor<Article>, // <T> class 의 모등 field 의 기본 검색기능을 추가함
        QuerydslBinderCustomizer<QArticle>  // interface mathod 로 상세 검색 규칙을 추가할 수 있게 만듬.
{

    Page<Article> findByTitleContaining(String title, Pageable pageable);
    Page<Article> findByContentContaining(String content, Pageable pageable);
    Page<Article> findByUserAccount_UserIdContaining(String userId, Pageable pageable);
    Page<Article> findByUserAccount_NicknameContaining(String nickname, Pageable pageable);
    Page<Article> findByHashtag(String hashtag, Pageable pageable);



    @Override
    default void customize(QuerydslBindings bindings, QArticle root) {
        bindings.excludeUnlistedProperties(true); // 기본 검색 모든 field 검색 기능을 가진 상태를 다시 제외 시키고 
        bindings.including(root.title, root.hashtag, root.content, root.createdAt, root.createdBy); // 검색을 원하는 field 만 다시 추가 시킴

        // 대소문자 구분없는 부분 글자 검색
//        bindings.bind(root.title).first(StringExpression::likeIgnoreCase);  // Query 생성이  like '${value}' -> 사용자가 % wild card 를 넣어 줘야 함.
        bindings.bind(root.title).first(StringExpression::containsIgnoreCase);  //            like '%${value}%'
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.hashtag).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.createdAt).first(DateTimeExpression::eq);
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);
    }
}

// QuerydslPredicateExecutor<Article> 을 하면 위 설명대로 기본 검색기능이 포함되지만 다음의 한계가 존재
// 1. 부분 검색 불가. exact match 만 검색 가능함.
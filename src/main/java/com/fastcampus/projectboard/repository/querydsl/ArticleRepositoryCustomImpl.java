package com.fastcampus.projectboard.repository.querydsl;

import com.fastcampus.projectboard.domain.Article;
import com.fastcampus.projectboard.domain.QArticle;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class ArticleRepositoryCustomImpl extends QuerydslRepositorySupport implements ArticleRepositoryCustom {

    public ArticleRepositoryCustomImpl() {
        super(Article.class);
    }

    @Override
    public List<String> findAllDistinctHashtags() {
        // 자동 생성된 QObject 를 사용??
        QArticle article = QArticle.article;

        return from(article)                        // Query 의 시작점을 정의. 즉 Article Entity 를 대상으로 Query 를 시작하고자 함.
                .distinct()                         // 중복된 record 제거
                .select(article.hashtag)            // Query 가 반환할 특정 column 지정
                .where(article.hashtag.isNotNull()) // Query filter 조건, 즉 hashtag field 가 Notnull 만 선택
                .fetch();                           // Query 결과를 가져옴.

        //        JPQLQuery<String> query = from(article)
//                .distinct()
//                .select(article.hashtag)
//                .where(article.hashtag.isNotNull());
//
//        return query.fetch();

    }
}

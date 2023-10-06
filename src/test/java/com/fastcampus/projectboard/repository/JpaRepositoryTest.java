package com.fastcampus.projectboard.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fastcampus.projectboard.domain.Article;
import com.fastcampus.projectboard.domain.ArticleComment;
import com.fastcampus.projectboard.domain.Hashtag;
import com.fastcampus.projectboard.domain.UserAccount;
import com.fastcampus.projectboard.repository.querydsl.HashtagRepositoryCustom;
import org.aspectj.weaver.ast.Instanceof;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import static org.assertj.core.api.Assertions.*;

//@ActiveProfiles("testdb")
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("JPA 연결 test")
@Import(JpaRepositoryTest.TestJpaConfig.class)  // 기존 JpaConfig.Class 를 대신 사용 (해당 test 에서만 Auditing 이 임의로 통과시키도록)
@DataJpaTest
class JpaRepositoryTest {

    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final UserAccountRepository userAccountRepository;
    private final HashtagRepository hashtagRepository;

    JpaRepositoryTest(
            @Autowired ArticleRepository articleRepository,
            @Autowired ArticleCommentRepository articleCommentRepository,
            @Autowired UserAccountRepository userAccountRepository,
            @Autowired HashtagRepository hashtagRepository
    ) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
        this.userAccountRepository = userAccountRepository;
        this.hashtagRepository = hashtagRepository;
    }

    @DisplayName("select Test")
    @Test
    void givenTestData_whenSelecting_thenWorkFine() {
        // Given

        // When
        List<Article> articles = articleRepository.findAll();

        // Then
        assertThat(articles)
                .isNotNull()
                .hasSize(123); // classpath:resources/data.sql 참조
    }

    @DisplayName("insert Test")
    @Test
    void givenTestData_whileInsert_thenWorkFine() {
        // Given
        long previousCount =  articleRepository.count();
        UserAccount userAccount = userAccountRepository.save(
                UserAccount.of(
                        "newUno", "pw", null, null, null
                ));
        Article article = Article.of(userAccount, "new article", "new content");
        article.addHashtags(Set.of(Hashtag.of("spring")));

        // When
        articleRepository.save(article);

        // Then
        assertThat(articleRepository.count()).isEqualTo(previousCount + 1);
    }

    @DisplayName("Updating Test")
    @Test
    void givenTestData_whenUpdating_thenWorksFine() {
        // Given
        Article article = articleRepository.findById(1L).orElseThrow();
        Hashtag updatedHashtag = Hashtag.of("springboot");
        article.clearHashtags();
        article.addHashtags(Set.of(updatedHashtag));

        // When
        Article savedArticle = articleRepository.saveAndFlush(article);

        // Then
        assertThat(savedArticle.getHashtags())
                .hasSize(1)
                .extracting("hashtagName", String.class)
                .containsExactly(updatedHashtag.getHashtagName());
    }

    @DisplayName("Deleting Test")
    @Test
    void givenTestData_whenDelete_thenWorkFine() {
        // Given
        Article article = articleRepository.findById(1L).orElseThrow();
        long previousArticleCount = articleRepository.count();
        long previousArticleCommentCount = articleCommentRepository.count();
        int deletedCommentsSize = article.getArticleComments().size();

        // When
        articleRepository.delete(article);

        // Then
        assertThat(articleRepository.count()).isEqualTo(previousArticleCount - 1);
        assertThat(articleCommentRepository.count()).isEqualTo(previousArticleCommentCount - deletedCommentsSize);
    }

    @DisplayName("대댓글 조회 테스트")
    @Test
    void givenParentCommentId_whenSelecting_thenReturnsChildComments() {
        // Given

        // When
        Optional<ArticleComment> parentComment = articleCommentRepository.findById(1L);

        // Than
        assertThat(parentComment).get()         // get() : Optional inner value 를 꺼내면서 null 인지 확인까지
                .hasFieldOrPropertyWithValue("parentCommentId", null) // 부모 자체는 해당 column value : null
                .extracting("childComments", InstanceOfAssertFactories.COLLECTION) // nested value 를 가져옴 (속성 InstanceOfAssertFactories.COLLECTION 은 추출한 data type check)
                .hasSize(4); // 4개의 대댓글이 확인 되어야.
    }

    @DisplayName("댓글에 대댓글 삽입 테스트")
    @Test
    void givenParentComment_whenSaving_thenInsertsChildComment() {
        // Given
        ArticleComment parentComment = articleCommentRepository.getReferenceById(1L);
        ArticleComment childComment = ArticleComment.of(
          parentComment.getArticle(),
          parentComment.getUserAccount(),
          "대댓글"
        );

        // When
        parentComment.addChildComment(childComment);
        articleCommentRepository.flush();   // "영속성 context 에 알려 주어야" flush()가 호출될 때, 영속성 컨텍스트에 존재하는 변경된 엔터티들의 상태가 실제 데이터베이스와 동기화되도록 JPA에 지시하는 것을 의미

        // Then
        assertThat(articleCommentRepository.findById(1L)).get()
                .extracting("childComments", InstanceOfAssertFactories.COLLECTION)
                .hasSize(5);
    }

    @DisplayName("댓글 삭제와 대댓글 전체 연동 삭제 테스트")
    @Test
    void givenArticleCommentHavingChildComments_whenDeletingParentComment_thenDeletesEveryComment() {
        // Given
        ArticleComment parentComment = articleCommentRepository.getReferenceById(1L);
        long previousArticleCommentCount = articleCommentRepository.count();

        // When
        articleCommentRepository.delete(parentComment);

        // Then
        assertThat(articleCommentRepository.count()).isEqualTo(previousArticleCommentCount - 5);
    }

    @DisplayName("댓글 삭제와 대댓글 전체 연동 삭제 테스트 - 댓글 ID + 유저 ID")
    @Test
    void givenArticleCommentsIdHavingChildCommentsAndUserId_whenDeletingParentComment_thenDeletesEveryComment() {
        // Given
        long previousArticleCommentCount = articleCommentRepository.count();

        // When
        articleCommentRepository.deleteByIdAndUserAccount_UserId(1L, "uno");

        // Then
        assertThat(articleCommentRepository.count()).isEqualTo(previousArticleCommentCount - 5);
    }

    @DisplayName("[QueryDSL] 전체 hashtag list 에서 이름만 조회하기")
    @Test
    void givenNothing_wehnQueryingHashtags_thenRetrunsHashtagNames() {
        // Given

        // When
        List<String> hashtagNames = hashtagRepository.findAllHashtagNames();

        // Then
        assertThat(hashtagNames).hasSize(19); // ? 왜 19 개???
    }

    @DisplayName("[QueryDSL] hashtag 로 paging 된 게시글 검색하기")
    @Test
    void givenHashtagNamesAndPageable_whenQueryingArticles_thenReturnsArticlePage() {
        // Given
        List<String> hashtagNames = List.of("blue", "crimson", "fuscia");
        Pageable pageable = PageRequest.of(0, 5, Sort.by(
                Sort.Order.desc("hashtags.hashtagName"),
                Sort.Order.asc("title")
        ));

        // When
        Page<Article> articlePage = articleRepository.findByHashtagNames(hashtagNames, pageable);

        // Then
        // TODO!!!!!
        assertThat(articlePage.getContent()).hasSize(pageable.getPageSize());
        assertThat(articlePage.getContent().get(0).getTitle())
                .isEqualTo("Fusce posuere felis sed lacus.");

        assertThat(articlePage.getContent().get(0).getHashtags())
                .extracting("hashtagName", String.class)
                .containsExactly("fuscia");

        assertThat(articlePage.getTotalElements()).isEqualTo(17);
        assertThat(articlePage.getTotalPages()).isEqualTo(4);
    }

    // Auditor 가 사용자 계정 인증을 확인하고 test 에서 인증이 필요한 작업에 대해서 작업 진행을 막고 있으므로
    // 필요한 test 에서만 Auditor 가 임의로 통과시키도록 설정
    @EnableJpaAuditing
    @TestConfiguration
    public static class TestJpaConfig {
        @Bean
        public AuditorAware<String> auditorAware() {
            return () -> Optional.of("uno");
        }
    }

}

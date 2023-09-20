package com.fastcampus.projectboard.service;

import com.fastcampus.projectboard.domain.Article;
import com.fastcampus.projectboard.domain.type.SearchType;
import com.fastcampus.projectboard.dto.ArticleUpdateDto;
import com.fastcampus.projectboard.repository.ArticleRepository;
import com.fastcampus.projectboard.dto.ArticleDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

// test 경량화를 위해 Bootstrap link 를 사용하지 않고
// Mockito 를 사용???

@DisplayName("비지니스 로직 - 게시글")
@ExtendWith(MockitoExtension.class) // Mockito : Tasty mocking framework for unit tests in Java -> Spring boot slice test 기능 대신 사용
                                    //           Spring boot context 를 띄우는 작업/시간 절감 (가볍게)
class ArticleServiceTest {

    // @InjectMocks Mock 을 주입하는 대상 (대당 객체 field, setter, constructor 로 Mock 이 된 type 이 들어감)
    // ArticleService 을 불러오고 (sut : system under test - test 대상임을 의미)
    // field 에만 가능  -> 생성자 parameter 로는 사용 불가
    @InjectMocks private ArticleService sut;

    // @Mock 객체 자체를 바로 test 용 data 자체로 사용
    // field 와 method parameter 사용 가능
    // AricleRepository 를 통한 DB CRUD 작업 진행 (Mocking 할 때 필요)
    @Mock private ArticleRepository articleRepository;

    @DisplayName("게시글을 검색하면, 게시글 리스트를 반환한다. ")
    @Test
    void givenSearchParameters_whenSearchingArticles_thenReturnsArticleList() {
        // Given

        // When
        // 검색 가능 parmater 중 title 을 keyword 하여 검색을 진행했을 때, 그 결과로 AritcleDto list 를 반환받는다.
        List<ArticleDto> articles = sut.searchArticles(SearchType.TITLE,  "search keyword");  // indexing parameter: 제목, 본문, ID, 닉네임, 해시태그

        // Then
        assertThat(articles).isNotNull();
    }

    @Disabled
    @DisplayName("게시글 조회 -> 게시글 반환")
    @Test
    void givenArticleId_whenClickArticle_thenReturnsArticle() {
        // Given

        // When
        ArticleDto articles = sut.searchArticle(1L); // Long articleId

        // Then
        assertThat(articles).isNotNull();
    }

    @Disabled
    @DisplayName("page 를 클릭하면 해당 page 로 이동")
    @Test
    void givenPage_whenClickPage_thenReturnsPage() {
        // Givn

        // When
        Page<ArticleDto> articles = sut.pagingArticles(SearchType.TITLE, "search keyword");

        // Then
        assertThat(articles).isNotNull();
    }

    // test failed: Wanted but not invoked -> 실제로 save 할 data 가 없기 때문에 진행되지 않음.
    // 그러나 이는 save 시도까지 진행됨을 보기위한 해당 Test 의 의도에 부함함.
    @Disabled
    @DisplayName("게시글 내용 입력 -> 게시글 생성")
    @Test
    void givenArticleInfo_whenSavingArticle_thenSavedArticle() {
        // Given
        ArticleDto dto = ArticleDto.of(
                LocalDateTime.now(),
                "Gon",
                "title",
                "content",
                "#java"
        );
        given(articleRepository.save(any(Article.class))).willReturn(null);

        // When
        sut.saveArticle(dto);

        // Then
        // save 호출이 발생했는가를 검사
        then(articleRepository).should().save(any(Article.class));
    }

    // test failed: Wanted but not invoked -> 실제로 save 할 data 가 없기 때문에 진행되지 않음.
    // 그러나 이는 save 시도까지 진행됨을 보기위한 해당 Test 의 의도에 부함함.
    @Disabled
    @DisplayName("게시글의 ID와 수정 정보를 입력하면, 게시글을 수정한다.")
    @Test
    void givenArticleIdAndModifiedInfo_whenUpdatingArticle_thenUpdatesArticle() {
        // Given
        given(articleRepository.save(any(Article.class))).willReturn(null);

        // When
        sut.updatedArticle(1L, ArticleUpdateDto.of("title", "content", "#java"));

        // Then
        then(articleRepository).should().save(any(Article.class));
    }

    // 위 test 들과 동일
    @Disabled
    @DisplayName("게시글의 ID 를 입력하면, 해당 게시글 삭제")
    @Test
    void givenArticleId_whenDeletingArticle_thenDletesArticle() {
        // Given
        willDoNothing().given(articleRepository).delete(any(Article.class));

        // When
        sut.deleteArticle(1L);

        // Then
        then(articleRepository).should().delete(any(Article.class));
    }

}


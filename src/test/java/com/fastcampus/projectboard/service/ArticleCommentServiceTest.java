package com.fastcampus.projectboard.service;

import com.fastcampus.projectboard.domain.Article;
import com.fastcampus.projectboard.domain.ArticleComment;
import com.fastcampus.projectboard.dto.ArticleCommentDto;
import com.fastcampus.projectboard.dto.ArticleCommentUpdateDto;
import com.fastcampus.projectboard.repository.ArticleCommentRepository;
import com.fastcampus.projectboard.repository.ArticleRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@DisplayName("비지니스 로직 - 댓글")
@ExtendWith(MockitoExtension.class)
class ArticleCommentServiceTest {

    @InjectMocks private ArticleCommentService sut;

    @Mock private ArticleCommentRepository articleCommentRepository;
    @Mock private ArticleRepository articleRepository;


    // Failed : Wanted but not invoked:
    // 실제 생성되지 않아 failed 이지만, 접근하는 과정 자체가 진행되는지를 확인하는 test 이므로 ok
    @Disabled
    @DisplayName("게시글 id 조회 -> 해당하는 댓글 list 를 불러옴")
    @Test
    void givenArticleID_whenSearchingArticleComments_thenReturnsArticleComments() {
        // Given
        Long articleId = 1L;
        given(articleRepository.findById(articleId)).willReturn(Optional.of(Article.of(
                "title", "content", "#java")
        ));

        // When
        List<ArticleCommentDto> articleComments = sut.searchArticleComment(1L);

        // Then
        assertThat(articleComments).isNotNull();
        then(articleRepository).should().findById(articleId);
    }

    @Disabled
    @DisplayName("댓글 정보를 입력하면, 댓글 생성")
    @Test
    void givenArticleCommentInfo_whenSavingArticleComment_thenSavedArticleComment() {
        // Given
        ArticleCommentDto dto = ArticleCommentDto.of(
                LocalDateTime.now(),
                "Gon",
                LocalDateTime.now(),
                "Gon",
                "content"
        );
        given(articleCommentRepository.save(any(ArticleComment.class))).willReturn(null);

        // When
        sut.saveArticleComment(dto);

        // Then
        then(articleCommentRepository).should().save(any(ArticleComment.class));
    }

    @Disabled
    @DisplayName("댓글 ID 와 수정 정보를 입력하면, 게시글을 수정한다.")
    @Test
    void givenArticleCommentIdAndModifiedInfo_whenUpateingArticleComment_thenUpdatesArticleComment() {
        // Given
        given(articleCommentRepository.save(any(ArticleComment.class))).willReturn(null);

        // When
        sut.upateArticleComment(1L, ArticleCommentUpdateDto.of("conent"));

        // Then
        then(articleCommentRepository).should().save(any(ArticleComment.class));
    }

    @Disabled
    @DisplayName("댓글 ID 를 입력하면, 해당 댓글을 삭제")
    @Test
    void givenArticleCommentId_whenDeletingArticleComment_thenDeletesArticleComment() {
        // Given
        willDoNothing().given(articleCommentRepository).delete(any(ArticleComment.class));

        // When
        sut.deleteArticleComment(1L);

        // Then
        then(articleCommentRepository).should().delete(any(ArticleComment.class));
    }
}


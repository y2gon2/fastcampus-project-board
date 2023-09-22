package com.fastcampus.projectboard.controller;

import com.fastcampus.projectboard.config.SecurityConfig;
import com.fastcampus.projectboard.dto.ArticleWithCommentsDto;
import com.fastcampus.projectboard.dto.UserAccountDto;
import com.fastcampus.projectboard.service.ArticleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View controller - 게시글")
@Import(SecurityConfig.class)       // 모든 page 에 대한 url 접근이 가능하도록 설정한 config 를 import 해줘서, 기존의  get test 들이 통과하도록 변경
@WebMvcTest(ArticleController.class) // Bean 을 생성할 class 지정
class ArticleControllerTest {

    // @Autowired 는 @MockBean  과 같은 field injection code 로 사용 불가
    private final MockMvc mvc;

    // 반대로 @MockBean 은 construction injection 으로 사용 불가
    @MockBean private ArticleService articleService;

    public ArticleControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("[view][GET] 게시글 리스트 (게시판 페이지) - 정상호출")
    @Test
    public void givenNothing_whenRequestingArticlesView_thenReturnsAritclesView() throws Exception{
        //Given
        // argument matcher 인데 field 중 일부만 matcher 를 할 수 없다
        // 그래서 이때 두번째 argument 가 '아무거나'가 아닌 'null' 이여야 한다.
        // 해당 조건에서 eq() method 를 사용하면 된다. ... 무슨소리인지 모르겠다...;;;
        given(articleService.searchArticles(eq(null), eq(null), any(Pageable.class))).willReturn(Page.empty());

        // When & Then
        mvc.perform(MockMvcRequestBuilders.get("/articles")) // 해당 url 요청에 대한 test??
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles")); // 서버에서 게시글 목록을 받았을 view 로 articles attribute 이 전달되었는지 확인
        then(articleService).should().searchArticles(eq(null), eq(null), any(Pageable.class));
    }

    @DisplayName("[view][GET] 게시글 상세 페이지 - 정상호출")
    @Test
    public void givenNothing_whenRequestingArticleView_thenReturnsArticleView() throws Exception {
        // Given
        Long articleId = 1L;
        given(articleService.getArticle(articleId)).willReturn(createArticleWithCommentsDto());

        // When & Then
        mvc.perform(MockMvcRequestBuilders.get("/articles/" + articleId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/detail"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attributeExists("articleComments"));
        then(articleService).should().getArticle(articleId);
    }

    @Disabled("TODO")
    @DisplayName("[view][GET] 게시글 검색 전용 페이지 - 정상 호출")
    @Test
    public void givenNothing_whenRequestingSearchingArticle_thenReturnsSearchedArticleView() throws Exception {
        // Given

        // When & Then
        mvc.perform(MockMvcRequestBuilders.get("/articles/search"))
                .andExpect(status().isOk())
                .andExpect(view().name("articles/search"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));
    }

    @Disabled("TODO")
    @DisplayName("[view] [GET] 게시글 hashtag 검색 전용 페이지 - 정상호출")
    @Test
    public void givenNothing_whenRequestingHashtagSearchingView_thenReturnsSearchedArticleView() throws Exception {
        // Given

        // When & Then
        mvc.perform(MockMvcRequestBuilders.get("/articles/search-hashtag"))
                .andExpect(status().isOk())
                .andExpect(view().name("articles/search-hashtag"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));
    }

    // ----------------- Fixture for Test -------------------
    
    private ArticleWithCommentsDto createArticleWithCommentsDto() {
        return ArticleWithCommentsDto.of(
                1L,
                createUserAccountDto(),
                Set.of(),
                "title",
                "content",
                "#java",
                LocalDateTime.now(),
                "gon",
                LocalDateTime.now(),
                "gon"
        );
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                1L,
                "gon",
                "pw",
                "gon@mail.com",
                "Gon",
                "memo",
                LocalDateTime.now(),
                "gon",
                LocalDateTime.now(),
                "gon"
        );
    }
}



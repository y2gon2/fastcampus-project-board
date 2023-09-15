package com.fastcampus.projectboard.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View controller - 게시글")
@WebMvcTest(ArticleController.class) // Bean 을 생성할 class 지정
class ArticleControllerTest {

    private final MockMvc mvc;

    public ArticleControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

//    @Disabled("TODO")
    @DisplayName("[view][GET] 게시글 리스트 (게시판 페이지) - 정상호출")
    @Test
    public void givenNothing_whenRequestingArticlesView_thenReturnsAritclesView() throws Exception{
        //Given

        // When & Then
        mvc.perform(MockMvcRequestBuilders.get("/articles"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles")); // 서버에서 게시글 목록을 받았을 view 로 articles attribute 이 전달되었는지 확인
    }

    @Disabled("TODO")
    @DisplayName("[view][GET] 단건 게시글 - 정상호출")
    @Test
    public void givenNothing_whenRequestingArticleView_thenReturnsArticleView() throws Exception {
        // Given

        // When & Then
        mvc.perform(MockMvcRequestBuilders.get("/articles/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/detail"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attributeExists("articleComments"));
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
}



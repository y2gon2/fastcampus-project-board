package com.fastcampus.projectboard.controller;


import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


// WebMvcTest 는 controller (& RestController) 에 대한 test 만 부분적으로 빠르게 진행하기 위한 annotation
// controller 관련 bean 만 생성되며, service 나 repository bean 은 생성되지 않으므로  전체적으로  response 까지 test 하려면
// @SpringBootTest 를 적용하며야 함.
//@WebMvcTest
//@Disabled("해당 Data REST Test 는 무겁고 불필요하므로 전체 test 에서 제외")
@Disabled("Spring Data REST 통합테스트는 불필요하므로 제외시킴")
@DisplayName("Data REST - API test")
@Transactional  // test 내 transaction 은 기본적으로 rollback 처리되어 원본 DB 에 영향 X
@SpringBootTest
@AutoConfigureMockMvc
class DataRestTest {

    private MockMvc mvc;

    DataRestTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("[api] 게시글 list 조회")
    @Test
    void givenNothing_whenRequestArticles_thenReturnsArticleJsonResponse() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/api/articles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")))
                .andDo(print());
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

    }

    @DisplayName("[api] 게시글 단건 조회")
    @Test
    void givenNothing_whenRequestArticles_thenReturn1ArticleJsonResponse() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/api/articles/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
    }

    @Disabled("TODO")
    @DisplayName("[api] 게시글 -> 댓글 리스트 조회")
    @Test
    void givenNothing_whenRequestArticles_thenReturnArticleCommentFromArticle() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/api/articles/1/articleCommentSet"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
    }

    @DisplayName("[api] 댓글 리스트 조회")
    @Test
    void givenNothing_whenRequestArticles_thenReturnArticleComment() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/api/articleComments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
    }

    @DisplayName("[api] 댓글 단건 조회")
    @Test
    void givenNothing_whenRequestArticles_thenReturn1ArticleComment() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/api/articleComments/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
    }

    @DisplayName("[api] 회원 관련 AIP 는 일체 제공하지 않는다")
    @Test
    void givenNothing_whenRequestingUserAccounts_thenThrowsException() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/api/userAccounts")).andExpect(status().isNotFound());
//        mvc.perform(post("/api/userAccounts")).andExpect(status().isNotFound());
//        mvc.perform(patch("/api/userAccounts")).andExpect(status().isNotFound());
//        mvc.perform(delete("/api/userAccounts")).andExpect(status().isNotFound());
//        mvc.perform(head("/api/userAccounts")).andExpect(status().isNotFound());
    }
}

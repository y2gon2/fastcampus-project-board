package com.fastcampus.projectboard.controller;


import com.fastcampus.projectboard.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View controller - 인증 ")
@Import(SecurityConfig.class)       // 모든 page 에 대한 url 접근이 가능하도록 설정한 config 를 import 해줘서, 기존의  get test 들이 통과하도록 변경
@WebMvcTest
public class AuthControllerTest {

    private final MockMvc mvc;

    public AuthControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("[view] [GET] login 페이지 - 정상 호출")
    @Test
    public void givenNothing_whenTryingToLogin_thenReturnsLoginView() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));

    }
}

package com.fastcampus.projectboard.controller;


import com.fastcampus.projectboard.config.SecurityConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Disabled
@DisplayName("View controller - 인증 ")
@Import(SecurityConfig.class)       // 모든 page 에 대한 url 접근이 가능하도록 설정한 config 를 import 해줘서, 기존의  get test 들이 통과하도록 변경
@WebMvcTest(AuthControllerTest.EmptyController.class)
class AuthControllerTest {

    private final MockMvc mvc;

    AuthControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @Disabled
    @DisplayName("[view] [GET] login 페이지 - 정상 호출")
    @Test
    public void givenNothing_whenTryingToLogin_thenReturnsLoginView() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));

    }

    // 어떤 컨트롤러도 필요하지 않은 test 임을 나타내기 위해 test 용 빈 component 를 사용.
    @TestComponent
    static class EmptyController {}
}

package com.fastcampus.projectboard.cotroller;

import com.fastcampus.projectboard.config.SecurityConfig;
import com.fastcampus.projectboard.controller.MainController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(MainController.class)
class MainControllerTest {

    private final MockMvc mvc;

    MainControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    void givenRootPath_whenRequestingRootPage_thenRedirectsToArticlesPage() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/"))
                .andExpect(status().is3xxRedirection());

    }
}
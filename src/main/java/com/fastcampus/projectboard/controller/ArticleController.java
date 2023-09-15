package com.fastcampus.projectboard.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/articles")
@Controller
public class ArticleController {
    @GetMapping
    public String articles(ModelMap map) {
        map.addAttribute("articles", List.of());
        return "articles/index";
    }

    @GetMapping("/{articleId}") // 우선 내부 로직 구현 없이 routing 작업에 대해서만 처리
    public String article(@PathVariable Long articleId, ModelMap map) {
        map.addAttribute("article", "article"); // TODO: 실제 데이터 구현시 변경
        map.addAttribute("articleComments", List.of());
        return "articles/detail";
    }
}

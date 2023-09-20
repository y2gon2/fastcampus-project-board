package com.fastcampus.projectboard.service;


import com.fastcampus.projectboard.domain.type.SearchType;
import com.fastcampus.projectboard.dto.ArticleDto;
import com.fastcampus.projectboard.dto.ArticleUpdateDto;
import com.fastcampus.projectboard.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor // 필수 field 에 대한 생성자를 자동으로 만들어주는 lombok annotation
@Transactional
@Service
public class ArticleService {

    private final ArticleRepository ariticleRepository;

    @Transactional(readOnly = true) // 검색기능으로 data 의 변경을 일어나지 않으므로
    public List<ArticleDto> searchArticles(SearchType searchType, String searchKeyword) {
        return List.of(); // test 용 empty data 반환
    }

    @Transactional(readOnly = true)
    public ArticleDto searchArticle(long l) {
        return null;
    }

    public Page<ArticleDto> pagingArticles(SearchType searchType, String searchKeyword) {
        return Page.empty();
    }

    public void saveArticle(ArticleDto dto) {
    }

    public void updatedArticle(long articleId, ArticleUpdateDto of) {
    }

    public void deleteArticle(long ArticleId) {
    }
}



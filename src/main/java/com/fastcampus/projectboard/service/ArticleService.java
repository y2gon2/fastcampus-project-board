package com.fastcampus.projectboard.service;


import com.fastcampus.projectboard.domain.Article;
import com.fastcampus.projectboard.domain.type.SearchType;
import com.fastcampus.projectboard.dto.ArticleDto;
import com.fastcampus.projectboard.dto.ArticleUpdateDto;
import com.fastcampus.projectboard.dto.ArticleWithCommentsDto;
import com.fastcampus.projectboard.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Slf4j
@RequiredArgsConstructor // 필수 field 에 대한 생성자를 자동으로 만들어주는 lombok annotation
@Transactional
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    @Transactional(readOnly = true) // 검색기능으로 data 의 변경을 일어나지 않으므로
    public Page<ArticleDto> searchArticles(SearchType searchType, String searchKeyword, Pageable pageable) {

        // 1. 검색어가 없거나 whitespace 만으로 채워진 경우.
        if (searchKeyword == null || searchKeyword.isBlank()) {
            // Page 내 method map 의 경우 stream 없이 바로 젹용 가능??
            // map(article -> ArticleDto.from(article)) 과 같이 lambda 표현식 대신
            // method reference '::' 를 사용하여 표현함.
            //
            // 추가적으로 이와 같이 작성된 코드에서 service code 에서 entity 를 직접 처리하지 않으므로써
            // 해당 코드 내애서 entity 가 노출되는 경우를 제거 함.
            return articleRepository.findAll(pageable).map(ArticleDto::from);
        }

        return switch (searchType) {
            case TITLE -> articleRepository.findByTitleContaining(searchKeyword, pageable).map(ArticleDto::from);
            case CONTENT -> articleRepository.findByContentContaining(searchKeyword, pageable).map(ArticleDto::from);
            case ID -> articleRepository.findByUserAccount_UserIdContaining(searchKeyword, pageable).map(ArticleDto::from);
            case NICKNAME -> articleRepository.findByUserAccount_NicknameContaining(searchKeyword, pageable).map(ArticleDto::from);
            case HASHTAG -> articleRepository.findByHashtag("#" + searchKeyword, pageable).map(ArticleDto::from);
        };
    }

    public ArticleWithCommentsDto getArticle(Long articleId) {
        return articleRepository.findById(articleId)
                .map(ArticleWithCommentsDto::from)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다. - articleId: " + articleId));
    }

    public void saveArticle(ArticleDto dto) {
        articleRepository.save(dto.toEntity());
    }

    public void updateArticle(ArticleDto dto) {
        try {
            Article article = articleRepository.getReferenceById(dto.id());
            if (dto.title() != null) { article.setTitle(dto.title()); }
            if (dto.content() != null) { article.setContent(dto.content()); }
            article.setHashtag(dto.hashtag());
        } catch (EntityNotFoundException e) {
            log.warn("게시글 업데이트 실패, 게시글을 찾을 수 없습니다. - dto: {}", dto);
        }

        // articleRepository.save(article); // -> save 명령 없어도 됨
        // class level Transactional 이 구현되어 있으므로,
        // method transactional 이 묶여 있는 상태임.
        // 따라서 해당 method logic 이 끝날때, 영속성 context 는 article entity 가
        // 변경되었음을 감지함.
        // 그리고 수정사항에 대한 query 를 날림.
    }

    public void deleteArticle(long articleId) {
        articleRepository.deleteById(articleId);
    }


}



package com.fastcampus.projectboard.service;

import com.fastcampus.projectboard.domain.Hashtag;
import com.fastcampus.projectboard.repository.HashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Transactional
@RequiredArgsConstructor
@Service
public class HashtagService {
    private final HashtagRepository hashtagRepository;

    @Transactional(readOnly = true)
    public Set<Hashtag> findHashtagsByNames(Set<String> hashtagNames) {
        return new HashSet<>(hashtagRepository.findByHashtagNameIn(hashtagNames));
    }

    public Set<String> parseHashtagNames(String content) {
        if (content == null) {
            return Set.of();
        }

        // \w : word [A-Za-z0-9_]
        Pattern pattern = Pattern.compile("#[\\w가-힣]+");
        Matcher matcher = pattern.matcher(content.strip());
        Set<String> result = new HashSet<>();

        // matcher.find() : pattern 에 정의한 정규식 조건에 맞는 형태가 발견되면
        while (matcher.find()) {
            result.add(matcher.group().replace("#", ""));
        }

        // result 를 복사한 immutable object 로 변경??? (그냥 result 를 보내도 되는데 불변성을 강조하기 위해서)
        return Set.copyOf(result);
    }

    // 특정 게시글 하나가 지워졌다고, 해당 글의 해시태그를 지우면 안된다. 
    // 하나의 해시태그가 이를 포함한 모든 게시글이 삭제 되었을 때 비로소 해당 해시태그가 지워져야 함.
    public void deleteHashtagWithoutArticles(Long hashtagId) {
        Hashtag hashtag = hashtagRepository.getReferenceById(hashtagId);

        if (hashtag.getArticles().isEmpty()) {
            hashtagRepository.delete(hashtag);
        }
    }
}

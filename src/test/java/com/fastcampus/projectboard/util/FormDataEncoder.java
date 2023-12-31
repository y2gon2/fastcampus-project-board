package com.fastcampus.projectboard.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@TestComponent
public class FormDataEncoder {

    // ObjectMapper
    // Jackson 라이브러리에서 제공하는 class
    // Java object 와 JSON 사이 변환을 담당
    private final ObjectMapper mapper;

    public FormDataEncoder(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    // 주어진 객체를 URL 쿼리 문자열 형태의 Form 데이터로 인코딩 하는데 필요한 로직을 제공
    public String encode(Object obj) {
        Map<String, String> fieldMap = mapper.convertValue(obj, new TypeReference<>() {});
        // TypeReference
        // Generic type 정보를 가지고 있어 객체를 변환할 때 사용됨.
        // 해당 code 에서 TypeReference 객체를 생성하여 ObjectMapper 에게 Map<String, String> 타입으로 변환하도록 지시

        MultiValueMap<String, String> valueMap = new LinkedMultiValueMap<>();
        // MultiValueMap (interface)
        // 하나의 key  에 여려 value 를 가질 수 있는 map
        // LinkedMultiValueMap

        valueMap.setAll(fieldMap);
        // MultiValueMap 의 구현체로 해당 code 에서 LinkedMultiValueMap 객체를 생성하여
        // fieldMap 내용을 가짐 (복사).

        return UriComponentsBuilder.newInstance()
                .queryParams(valueMap)
                .encode()
                .build()
                .getQuery();
        // 해당 code 에서 valueMap 의 내용을 URL 쿼리 문자열로 인코딩하고 반환함.
        //
        // 1. UriComponentsBuilder
        // Uri 를 구성하고 조작할 수 있는 유틸리티를 제공
        // URI를 생성하거나 수정할 수 있도록 하는 Spring utility 클래스
        //
        // 2. queryParams(valueMap)
        // MultiValueMap을 받아 해당 맵에 있는 모든 쿼리 파라미터를 추가
        // 예를 들면, 만약 valueMap에 "key1=value1&key2=value2"와 같은 데이터가 있다면, 이를 쿼리 파라미터로 URI에 추가
        //
        // 3. encode()
        // URI 구성 요소를 인코딩합니다. 예를 들면, 공백은 %20으로, 특수 문자는 해당하는 인코딩 값으로 변환
        // URI를 안전하게 만들어주며, 유효한 URI만 전송되도록 함.
        //
        // 4. build()
        // 지금까지의 설정을 바탕으로 UriComponents 객체를 생성
        //
        // 5. getQuery()
        // UriComponents 객체에서 쿼리 부분만을 반환
        // 예를 들어, 전체 URI가 "http://example.com/page?key1=value1&key2=value2"라면,
        // getQuery()는 "key1=value1&key2=value2" 부분만을 반환
    }
}

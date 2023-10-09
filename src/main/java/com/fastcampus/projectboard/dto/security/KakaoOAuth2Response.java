package com.fastcampus.projectboard.dto.security;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

// TODO: Map -> Object 변환 로직이 있어 generic type casting 문제를 무시한다. 더 좋은 방법이 있다면 고려할 수 있음
// 인증 응답 정보
// https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info-response-body
@SuppressWarnings("unchecked")
public record KakaoOAuth2Response(
        Long id,
        LocalDateTime connectedAt,
        Map<String, Object> properties,
        KakaoAccount kakaoAccount
) {
    // https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#kakaoaccount
    public record KakaoAccount(
        Boolean profileNicknameNeedsAgreement,
        Profile profile,
        Boolean hasEmail,
        Boolean emailNeedsAgreement,
        Boolean isEmailValid,
        Boolean isEmailVerified,
        String email
    ) {
        // https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#profile
        public record Profile(String nickname) {
            public static Profile from(Map<String, Object> attributes) {
                return new Profile(String.valueOf(attributes.get("nickname")));
            }
        }

        public static KakaoAccount from(Map<String, Object> attributes) {
            return new KakaoAccount(
                    Boolean.valueOf(String.valueOf(attributes.get("profile_nickname_nees_agreement"))),
                    Profile.from((Map<String, Object>) attributes.get("profile")),
                    Boolean.valueOf(String.valueOf(attributes.get("has_email"))),
                    Boolean.valueOf(String.valueOf(attributes.get("email_needs_agreement"))),
                    Boolean.valueOf(String.valueOf(attributes.get("is_email_valid"))),
                    Boolean.valueOf(String.valueOf(attributes.get("is_email_verified"))),
                    String.valueOf(attributes.get("email"))
            );
        }

        public String nickname() { return this.profile().nickname(); }
    }

    public static KakaoOAuth2Response from(Map<String, Object> attributes) {
        return new KakaoOAuth2Response(
                Long.valueOf(String.valueOf(attributes.get("id"))),
                LocalDateTime.parse(
                        String.valueOf(attributes.get("connected_at")),
                        DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault())
                ),
                (Map<String, Object>) attributes.get("properties"),
                KakaoAccount.from((Map<String, Object>) attributes.get("kakao_account"))
        );
    }

    // 자주 사용할 getter 를 경로를 단순화 하여 접근할 수 있도록 추가로 정의함.
    public String email() { return this.kakaoAccount().email(); }
    public String nickname() { return this.kakaoAccount().nickname(); }
}

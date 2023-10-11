package com.fastcampus.projectboard.config;

import com.fastcampus.projectboard.dto.security.BoardPrincipal;
import com.fastcampus.projectboard.dto.security.KakaoOAuth2Response;
import com.fastcampus.projectboard.repository.UserAccountRepository;
import com.fastcampus.projectboard.dto.UserAccountDto;
import com.fastcampus.projectboard.service.UserAccountService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import java.util.UUID;

@Configuration
public class SecurityConfig {

    // secruity 에 태워서 security 에 관리하에 두고 인증과 권한 체크 진행
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService
    ) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // static resource 에 대해서 "인증" 을 검사 없이 모두 허가
                        .mvcMatchers(
                                HttpMethod.GET,
                                "/",
                                "/articles",
                                "/articles/search-hashtag"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults()) // 기본값을 내부적으로 설정. 따라서 method chaining 을 위한 and() 가 필요 없어짐.
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .oauth2Login(oAuth -> oAuth
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService)
                        )
                )
                .build();
    }

    // 아예 spring security 대상에서 제외할 내용들. -> static resource (JS, CSS)
    /*
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring().antMatchers("/css");

        // 위와 같이 제외 항목을 지정하여 제외시킬 수도 있지만,
        // static resource 가 spring 내에서 보편적으로 저장되는 경로가 있으므로 해당 경로를 잡아줌.
        //
        // but!! 해당 조건으로 실행하면 다음의 warning log 가 발생한다.
        //
        // WARN 1912 --- [  restartedMain] o.s.s.c.a.web.builders.WebSecurity
        //     : You are asking Spring Security to ignore org.springframework.boot.autoconfigure.security.servlet.StaticResourceRequest$StaticResourceRequestMatcher@464f3748.
        //       This is not recommended -- please use permitAll via HttpSecurity#authorizeHttpRequests instead.
        //
        // -> ignoring 은 Spring Security 모든 기능을 사용하지 않는 것임.
        //    따라서 (현재 프로그램 수준에선 문제가 없지만) csrf 와 같은 공격에 취약해 질 수 있음.
        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
    */


    // spring - security 를 사용한 사용자 정보 설정
    // 아래 두개의 method 가 구현되면, (기본적인) spring security 가 지원하는
    // 암호와 module 을 사용할 수 있게 된다.

    @Bean
    public UserDetailsService userDetailsService(UserAccountService userAccountService) {
        // username -> id -> userAccountDto -> BoardPrincipal
        // UserDetailsService interface method 인 loadUserByUsername 을 아래와 같이 구현한 것임.
        return username -> userAccountService
                .searchUser(username)
                .map(BoardPrincipal::from)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다. - username " + username));
    }

    /**
     * <p>
     * OAuth 2.0 기술을 이용한 인증 정보를 처리한다.
     * 카카오 인증 방식을 선택.
     *
     * <p>
     * TODO: 카카오 도메인에 결합되어 있는 코드. 확장을 고려하면 별도 인증 처리 서비스 클래스로 분리하는 것이 좋지만, 현재 다른 OAuth 인증 플랫폼을 사용할 예정이 없어 이렇게 마무리한다.
     *
     * @param userAccountService  게시판 서비스의 사용자 계정을 다루는 서비스 로직
     * @param passwordEncoder 패스워드 암호화 도구
     * @return {@link OAuth2UserService} OAuth2 인증 사용자 정보를 읽어들이고 처리하는 서비스 인스턴스 반환
     */
    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService(
            UserAccountService userAccountService,
            PasswordEncoder passwordEncoder
    ) {
        // OAuth2.0 REST API provider 정보를 받아주는 SpringSecrurity OAuth2 기능을 사용할 변수 선언
        // spring security OAuth 기본 구현체
        // bean 으로 자동 등록 X -> 기본적으로 parameter 로 Injection 하여 사용하지 않고 생성자로 직접 만들어 줘야
        final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

        // OAuth2UserService interface 는 아래 method 를 가진 functionalInterface 이다.
        // U loadUser(R userRequest) throws OAuth2AuthenticationException;
        // 따라서 아래 lambda 식은 loadUser method 의 구현체 이다.
        // lambda 표현식 parameter 는 해당 구현체의 parameter 인 userRequest 를 말하는 것이며
        // 해당 fucntion interface 에 사용된 generic R U 에 대해서는 현재 method 반환 type <> 에
        // 각각 OAuth2UserRequest, OAuth2User 로 정의되어 있으므로
        // userRequest 는 곧 R : OAuth2UserRequest 임을 알 수 있다.
        return userRequest -> {
            // Spring Security OAuth2 서비스를 통해 provider 가 응답한 사용자 정보를 load
            OAuth2User oAuth2User = delegate.loadUser(userRequest);

            // provider 마다 parsing 구조, 내요이 다르므로 이를 정의한 class 를 통해 유의미한 형태로 encoding 한 data 생성
            KakaoOAuth2Response kakaoResponse = KakaoOAuth2Response.from(oAuth2User.getAttributes());
            // 사용자 이름은 에초에 OAuth API 로 가져올수 없기 때문에 우리가 생성해줘야 함 (?)

            // yaml 파일에서 security.oauth2.client.registration 으로 설정했던 provider (kakao) 를 의미
            String registrationId = userRequest.getClientRegistration().getRegistrationId(); // "kakao"
            String providerId = String.valueOf(kakaoResponse.id());
            String username = registrationId + "_" + providerId;
            // 회원 여부 인증은 kakao 에서 처리하므로 사실 password 는 필요가 없다.
            // 다만 해당 project 에서는 최초 password 가 설계에 포함되었었기 때문에 이를 고려하여
            // 수정하지 않고 dummy password 를 넣어줌.
            String dummyPassword = passwordEncoder.encode("{bcrypt}dummy" + UUID.randomUUID());

            // DB 에 userAccount 존재 -> Ok 아니면 저장
            return userAccountService.searchUser(username)
                    .map(BoardPrincipal::from) // 있다면 인증 정보 생성 내보냄.
                    .orElseGet(() ->
                            BoardPrincipal.from( // 없다면 생성하여 내보냄.
                                    userAccountService.saveUser(
                                            username,
                                            dummyPassword,
                                            kakaoResponse.email(),
                                            kakaoResponse.nickname(),
                                            null
                                    )
                            )
                    );
        };
    }

    // 앞에 method 에서 PasswordEncoder 를 DI 적용하기 위한 method 이다.
    // Spring Security 에 정의된 PasswordEncoder interface 를 반환타입으로 정의하여
    // @Bean method 로 정의하여, Spring ApplicationContext 에 Bean 으로 등록 시킴으로써
    // DI 적용이 가능해진다.
    // (config class 내 method 대부분 사용자 정의 또는 DI 적용을 위한 bean method 들을 정의하는 곳 이였음. )
    @Bean
    public PasswordEncoder passwordEncoder() {
        // PasswordEnoderFactories.createDelegatingPasswordEncoder()
        // 여러가지 비밀번호 인코딩 방식을 지원하기 위한 PasswordEncoder 의 구현체인
        // DelegatingPasswordEncoder 를 생성함.
        // createDelegatingPasswordEncoder의 주요 특징
        // 1. 다양한 인코딩 방식 지원: 이 메서드를 통해 생성된 DelegatingPasswordEncoder는 여러 인코딩 방식(예: bcrypt, scrypt, pbkdf2 등)을 지원
        // 2. 접두어를 사용한 인코딩 식별: 저장된 비밀번호 값 앞에 {...} 형식의 접두어를 붙여, 해당 비밀번호가 어떤 인코딩 방식으로 인코딩되었는지 식별.
        //    예를 들어, {bcrypt}... 접두어는 비밀번호가 bcrypt 방식으로 인코딩되었음을 나타냄.
        //    기본적으로 bcrypt 방식을 사용하여 비밀번호를 인코딩
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}

package com.fastcampus.projectboard.config;

import com.fastcampus.projectboard.dto.security.BoardPrincipal;
import com.fastcampus.projectboard.repository.UserAccountRepository;
import com.fastcampus.projectboard.dto.UserAccountDto;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // secruity 에 태워서 security 에 관리하에 두고 인증과 권한 체크 진행
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
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
                .formLogin().and()
                .logout()
                    .logoutSuccessUrl("/")
                    .and()
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
    public UserDetailsService userDetailsService(UserAccountRepository userAccountRepository) {
        // username -> id -> userAccountDto -> BoardPrincipal
        // UserDetailsService interface method 인 loadUserByUsername 을 아래와 같이 구현한 것임.
        return username -> userAccountRepository
                .findById(username)
                .map(UserAccountDto::from)
                .map(BoardPrincipal::from)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다. - username " + username));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // passwordEncoder 설정을 위임해서 factory 로 부터 가져오겠다.
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}

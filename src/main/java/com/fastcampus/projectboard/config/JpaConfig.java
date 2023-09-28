package com.fastcampus.projectboard.config;


import com.fastcampus.projectboard.dto.security.BoardPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@EnableJpaAuditing
@Configuration
public class JpaConfig {

    // 현재 사용자 정보를 별도로 생성하지 않는 상태에서 사용자 정보가 entity 에서 필요하므로,
    // 이런 경우 여기에서 관련 생성 힘수를 AuditorAware 함수의 반환 값으로 넣어줌.
    @Bean
    public AuditorAware<String> auditorAware() {
        // SecurityContextHolder : 모든 인증 정보를 가지고 있는 class
        return () -> Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .map(BoardPrincipal.class::cast)
                .map(BoardPrincipal::getUsername);  // map(x -> (BoardPrincipal) x) -> typecasting
        // 1. spring sercurlty 사용시 Security 모든 정보를 들고 있는 SecurityContextHolder class 로부터 context 정보를 얻는다.
        // 2. 그 안에 Authentication 정보를 가져온다.
        // 3. 현재 user 가 인증 되었는지 (로그인 상태인지) 확인
        // 4. 로그인 정보(principal interface)를 꺼내온다.
        //    다만, getPrincipal 에는 어떤 인증 정보를 사용할지 모르기 때문에 Object type 상태이다.
        // 5. principa 구현체인 BoardPrincipal 로 typecasting  한다.
        //    해당 class 는 이미 Spring security Authentication/Authorization process 수행에 필요한 UserDetail 의 구현체 이므로
        //    BoardPrincipal 로 typecasing 해도 문제 없다
        // 6. 최종적으로 BoardPrincipal 에서 사용자 이름을 가져온다.


    }
}

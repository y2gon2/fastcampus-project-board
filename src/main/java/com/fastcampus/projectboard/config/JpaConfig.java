package com.fastcampus.projectboard.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@EnableJpaAuditing
@Configuration
public class JpaConfig {

    // 현재 사용자 정보를 별도로 생성하지 않는 상태에서 사용자 정보가 entity 에서 필요하므로,
    // 이런 경우 여기에서 관련 생성 힘수를 AuditorAware 함수의 반환 값으로 넣어줌.
    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of("gon"); // TODO : sprint security 로 인증 기능을 붙이게 될 때, 수정해야 함.
    }
}

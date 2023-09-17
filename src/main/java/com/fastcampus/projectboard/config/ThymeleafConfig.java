package com.fastcampus.projectboard.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;

@Configuration
public class ThymeleafConfig {

    @Bean
    public SpringResourceTemplateResolver thymeleafTemplateResolver(
            SpringResourceTemplateResolver defaultTemplateResolver,
            Thymeleaf3Properties thymeleaf3Properties
    ) {
        defaultTemplateResolver.setUseDecoupledLogic(thymeleaf3Properties.isDecoupledLogic()); // application.yaml 에서 thymeleaf3.decoupled-logic: true  로 setting

        return defaultTemplateResolver;
    }


    @RequiredArgsConstructor
    @Getter
    @ConstructorBinding
    @ConfigurationProperties("spring.thymeleaf3") // configuration property 를 유저가 직접 만든 경우 반드시 scan 이 필요
    public static class Thymeleaf3Properties {
        /**
         * Use Thymeleaf 3 Decoupled Logic
         */
        private final boolean decoupledLogic;

        // lombok @RequiredArgsConstructor 로 대체 가능
//        public Thymeleaf3Properties(boolean decoupledLogic) {
//            this.decoupledLogic = decoupledLogic;
//        }

        // lombok  @Getter 으로 데체 가능
//        public boolean isDecoupledLogic() {
//            return this.decoupledLogic;
//        }
    }

}


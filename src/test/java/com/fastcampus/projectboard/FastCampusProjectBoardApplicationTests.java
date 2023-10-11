package com.fastcampus.projectboard;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// Atice
// SprigBootTest 로 진행 시,SpringBoot 의 모든 설정과 bean 생성을 실행한다.
// 그런데 현재 application.yaml 파일에 민감정보를 명시하지 않고 환경 변수에서 가저오도록 설정함으로써,
// test 과정 error 가 발생하게 된다.
// 따라서, application.yaml 에서 test 용 설정을 명시하고 실행 profile 을 "test" 로 진행할 수 있도록 annotation 을 붙임
@ActiveProfiles("test")
@SpringBootTest
class FastCampusProjectBoardApplicationTests {

    @Test
    void contextLoads() {
    }

}

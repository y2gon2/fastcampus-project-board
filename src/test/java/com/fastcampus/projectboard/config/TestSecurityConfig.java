package com.fastcampus.projectboard.config;

import com.fastcampus.projectboard.dto.UserAccountDto;
import com.fastcampus.projectboard.service.UserAccountService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

// JpaRepositoryTest Insert Test 실패에서 해당 class 의 필요성을 확인할 수 있어짐.
// 해당 test 에서 임의 user 정보로 user account 영속성을 얻고자 한다.
// 그러나 SecurityConfig 에서 UserAccountRepository 에 접근하여 기존 ID 릴 가져와서 인증 작업을 진행하고 있으므로
// Test 를 통과할 수 없는 구조가 만들어진다.
// 이를 해결하기 위해서 TestSecurityConfig 를 별도 구성하여
// Test 과정에서도 인증 과정을 그대로 사용할 수 있는 조건을 만들어줘야 한다.
// 해당 code 에서는 test 용 인증이 통과 가능한 data 를 별도로 만들어 주고 이를 사용하는 방법으로 구성함.


@Import(SecurityConfig.class)
public class TestSecurityConfig {


    @MockBean private UserAccountService userAccountService;

    // 각 spring test 를 실행하기 전에 해당 method 를 실행하여 인증된 사용자계정을 넣어줌??
    // Spring test (ex. spring slice test / spring boot test) 를 사요한다면 @BeforTestMethod annotation  사용 가능
    @BeforeTestMethod
    public void securitySetUp() {
        given(userAccountService.searchUser(anyString()))
                .willReturn(Optional.of(createUserAccountDto()));

        given(userAccountService
                .saveUser(
                        anyString(),
                        anyString(),
                        anyString(),
                        anyString(),
                        anyString()
                )
        ).willReturn(createUserAccountDto());
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                "unoTest",
                "pw",
                "uno-test@email.com",
                "uno-test",
                "test memeo"
        );
    }

    // @MockBean private UserAccountRepository userAccountRepository;
    // OAuth2 설정 전까지 UserAccountService 가 없었기 때문에 직접 repository 를 mocking 하고
    // 아래와 같이 test 용 계정을 심어서 사용하고 있었음
    // 그러나 OAuth2 를 통한 인증 및 UserAccount table 접근 작업을 진행하기 위한 UserAccountService 를 구현하고
    // 이를 통한 business logic 을 통한 작업이 필요해짐에 따라 해당 방법을 더이상 test 가 불가능하게 됨.
    //
//    @BeforeTestMethod
//    public void securitySetUp() {
//        given(userAccountRepository.findById(anyString())).willReturn(Optional.of(UserAccount.of(
//                "unoTest",
//                "pw",
//                "uno-test@email.com",
//                "uno-test",
//                "test memeo"
//        )));

}

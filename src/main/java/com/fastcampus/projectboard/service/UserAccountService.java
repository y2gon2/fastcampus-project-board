package com.fastcampus.projectboard.service;

import com.fastcampus.projectboard.domain.UserAccount;
import com.fastcampus.projectboard.dto.UserAccountDto;
import com.fastcampus.projectboard.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;

    // return type 을 Optional 로 사용하는 이유
    // Optrional 이 아닌 바로 UserAccountDto type 으로 반환하는 경우,
    // mapping 이후 username 을 찾지 못했거나 mapping 에 맞지 않는 경우에 대한 처리를 위해
    // orElseThrow() 등을 사용하여 exception  처리르 해주어야 한다.
    // 그러나 Optional 로 wrapping 하여 보내면 null 인 경우에도 예외 상황 발생없이 처리할 수 있다.
    // 이렇게 작업할 경우, 발생하는 예외를 해당 반환값을 받는 method 로 처리를 위임 하기 위함.
    // 그 이유는 해당 단계의 error 는 논리적으로 NotFound (from DB) 이다.
    // 그러나 의미적으로 해당 작업의 실패는 "인증 실패" error 를 던지는 것이 더 적절하다고 할 수 있다.
    // 따라서 해당 method 는 인증 method 에서 호출할 것이므로, 해당 method 에서 '인증실패' exception 을 보내느것이  더 적절하다.
    @Transactional(readOnly = true)
    public Optional<UserAccountDto> searchUser(String username) {
        return userAccountRepository.findById(username)
                .map(UserAccountDto::from);
    }

    public UserAccountDto saveUser(
            String username,
            String password,
            String email,
            String nickname,
            String memo
    ) {
        return UserAccountDto.from(
                userAccountRepository.save(
                        UserAccount.of(username, password, email, nickname, memo, username)
                )
        );
    }

}

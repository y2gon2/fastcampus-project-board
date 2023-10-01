package com.fastcampus.projectboard.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;


@Getter
@ToString
@Table(indexes = {
        @Index(columnList = "email", unique = true),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity
public class UserAccount extends AuditingFields {
    @Id @Column(length = 50) private String userId;

    @Setter @Column(nullable = false) private String userPassword;

    @Setter @Column(length = 100) private String email;
    @Setter @Column(length = 100) private String nickname;
    @Setter private String memo;

    protected UserAccount() {}

    private UserAccount(String userId, String userPassword, String email, String nickname, String memo) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.email = email;
        this.nickname = nickname;
        this.memo = memo;
    }

    public static UserAccount of(String userId, String userPassword, String email, String nickname, String memo) {
        return new UserAccount(userId, userPassword, email, nickname, memo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAccount that = (UserAccount) o;
        return Objects.equals(this.getUserId(), that.getUserId());  // 직접 field 조회에서 getter 사용으로 변경. proxy 객체 직접 접근시 발생하는 문제 해결 ?
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getUserId());  // 직접 field 조회에서 getter 사용으로 변경. proxy 객체 직접 접근시 발생하는 문제 해결 ?
    }
}

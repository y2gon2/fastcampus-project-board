package com.fastcampus.projectboard.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.catalina.User;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.*;

@Getter
@ToString(callSuper = true)
@Table(indexes = {
        @Index(columnList = "content"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy"),
})
//@EntityListeners(AuditingEntityListener.class)     // AuditingFields 로 빠짐
@Entity
public class ArticleComment extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ManyToOne anntation 에 cascade option -> 해당 entity (댓글)의 변화가 해당 member type (Aritcle) 에 영향을 주어야 하는가?
    // -> 그렇지 않으므로 cascade 는 기본 설정 none 적용 (생략되어 있음.)
    @Setter @ManyToOne(optional = false) private Article article; // 연관관계 설정 필요
    @Setter @ManyToOne(optional = false) @JoinColumn(name = "userId") private UserAccount userAccount; // 유저 정보 (ID)

    @Setter @Column(nullable = false, length = 500) private String content;

    // AuditingFields 로 빠짐
//    @CreatedDate @Column(nullable = false) private LocalDateTime createdAt;
//    @CreatedBy @Column(nullable = false, length = 100) private String createdBy;
//    @LastModifiedDate @Column(nullable = false) private LocalDateTime modifiedAt;
//    @LastModifiedBy  @Column(nullable = false, length = 100) private String modifiedBy;

    protected ArticleComment() {}

    private ArticleComment(Article article, UserAccount userAccount, String content) {
        this.article = article;
        this.userAccount = userAccount;
        this.content = content;
    }

    public static ArticleComment of(Article article, UserAccount userAccount, String content) {
        return  new ArticleComment(article, userAccount, content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArticleComment that = (ArticleComment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

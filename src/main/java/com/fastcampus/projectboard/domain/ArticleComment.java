package com.fastcampus.projectboard.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

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
    @Setter
    @ManyToOne(optional = false)
    private Article article; // 연관관계 설정 필요

    @Setter
    @ManyToOne(optional = false)
    @JoinColumn(name = "userId")
    private UserAccount userAccount; // 유저 정보 (ID)

    @Setter
    @Column(updatable = false)  // 한번 value 가 설정되면 수정 불가
    private Long parentCommentId; // 부모 댓글 ID, 단방향 mapping

    // 부모 comment 든 자식 comment 를 볼 수 있어야하므로
    @ToString.Exclude
    @OrderBy("createdAt ASC")
    @OneToMany(mappedBy = "parentCommentId", cascade = CascadeType.ALL)
    private Set<ArticleComment> childComments = new LinkedHashSet<>();

    @Setter @Column(nullable = false, length = 500) private String content;

    // AuditingFields 로 빠짐
//    @CreatedDate @Column(nullable = false) private LocalDateTime createdAt;
//    @CreatedBy @Column(nullable = false, length = 100) private String createdBy;
//    @LastModifiedDate @Column(nullable = false) private LocalDateTime modifiedAt;
//    @LastModifiedBy  @Column(nullable = false, length = 100) private String modifiedBy;

    protected ArticleComment() {}

    private ArticleComment(Article article, UserAccount userAccount, Long parentCommentId, String content) {
        this.article = article;
        this.userAccount = userAccount;
        this.content = content;
        this.parentCommentId = parentCommentId;
    }

    public static ArticleComment of(Article article, UserAccount userAccount, String content) {
        return  new ArticleComment(article, userAccount, null, content);
    }

    public void addChildComment(ArticleComment child) {
        child.setParentCommentId(this.getId());
        this.getChildComments().add(child);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArticleComment that = (ArticleComment) o;
        return Objects.equals(this.getId(), that.getId());  // 직접 field 조회에서 getter 사용으로 변경. proxy 객체 직접 접근시 발생하는 문제 해결 ?
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());  // 직접 field 조회에서 getter 사용으로 변경. proxy 객체 직접 접근시 발생하는 문제 해결 ?
    }
}

package com.fastcampus.projectboard.domain;

import lombok.Getter;
import lombok.ToString;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.*;

// @Setter 를 @Getter 와 같이 전체 entity 에 걸수 도 있지만,
// 그렇게 할 경우, 모든 field value 가 외부에서 setting 이 가능해진다.
// 해당 project 에서 id 값의 경우, 시스템에서 부여되는 값을 그대로 사용하고 외부에서 값을 설정하지 못하도록
// 이와 같이 각각 필요한 field 에만 붙여줌.
@Getter
@ToString(callSuper = true)
@Table(indexes = { // 검색 가능한 column 들?
        @Index(columnList = "title"),
        @Index(columnList = "hashtag"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy"),
})
//@EntityListeners(AuditingEntityListener.class) // AuditingFields 로 빠짐
@Entity
public class Article extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // GeneratedValue -> auto increment 기능 사용
    // GenerationType.IDENTITY : mySQL auto increment 방식

    // 여러 Article 들이 하나의 userAccount 를 가질 수 있음.
    @Setter @ManyToOne(optional = false) @JoinColumn(name = "userId") private UserAccount userAccount; // 유저 정보 (ID)

    @Setter @Column(nullable = false) private String title;   // notNull
    @Setter @Column(nullable = false, length = 10000) private String content; // notNull

    @Setter private String hashtag; // nullable

    // 양방향 data (one to many)
    // 이 article 에 연동되어 있는 모든 comment 들을 중복되지 않게 모아서 collection 으로 보겠다?
    @ToString.Exclude
    @OrderBy("createdAt DESC")  /// 시간순 정렬??
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>(); // 양방향 binding 으로 게시글에 연관된 댓글 리스트를 출력하기 위한 코드

    // meta data
    //
    // 아래 field 는 자동 생성  -> jpaAuditing 기능 사용
    // 앞에 붙은 annotation 따라 자동 생성
    // 예를 들면 CreatedDate 는 생성되는 순간 해당 date 값을 자둉으로 넣어줌
    // 그런데 생성자 정보는 어떻게 얻지? login 같은건 아직 구현되어 있지 않은데?
    // -> JpaConfig 에서 설정??
    // -> AuditingField 로 빠짐
//    @CreatedDate @Column(nullable = false) private LocalDateTime createdAt;
//    @CreatedBy @Column(nullable = false, length = 100) private String createdBy;
//    @LastModifiedDate @Column(nullable = false) private LocalDateTime modifiedAt;
//    @LastModifiedBy @Column(nullable = false, length = 100) private String modifiedBy;

    // 모든 JPA (hibernate 기준) entity 들은 기본 생성자 구현이 필수
    protected Article() {}

    // id & metadata 제외 data 만 넣는 생성자 -> factory method 에서만 접근 가능하도록
    private Article(UserAccount userAccount, String title, String content, String hashtag) {
        this.userAccount = userAccount;
        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
    }

    public static Article of(UserAccount userAccount, String title, String content, String hashtag) {
        return new Article(userAccount, title, content, hashtag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return Objects.equals(id, article.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}


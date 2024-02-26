package com.fastcampus.projectboard.domain;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter //클래스의 필드에 대한 Getter메서드를 자동으로 생성해준다.
@ToString(callSuper = true) //부모클래스의 필드도 포함하여 문자열로 출력하도록 지정
@Table(indexes = {
        @Index(columnList = "title"),
        @Index(columnList = "hashtag"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
// 특정 테이블에 대한 세부 정보를 지정할 때 사용
@Entity // 테이터베이스의 테이블과 일치하는 엔티티로써 사용되며,
        // JPA에서는 해당 클래스를 데이터베이스와 매핑하여 데이터베이스 작업을 수행
public class Article extends AuditingFields {
    @Id // 해당 필드가 엔티티의 기본키임을 나타낸다.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 기본 키 값을 자동으로 행성하는 방법을 지정
    // `GenerationType.IDENTITY`는 데이터베이스의 자동 증가(AI) 기능을 사용하여 기본 키 값을 생성하도록 지정
    private Long id;

    @Setter
    @ManyToOne(optional = false)
    @JoinColumn(name = "userId")
    private UserAccount userAccount; // 유저 정보 (ID)   .
    // @Setter : 해당 필드에 대한 setter메서드를 자동으로 생성
    // @ManyToOne(optional = false) : 다대일 관계를 나타냅니다, `optional=false` 옵션은 이 관계가 반드시 존재해야 함을 나타낸다.
    // @JoinColumn(name = "userId") : 외래 키(Foreign Key)를 지정하는데 사용
        // 여기서 `name = "userId"`는 외래 키 열의 이름을 지정

    @Setter @Column(nullable = false) private String title; // 제목
    @Setter @Column(nullable = false, length = 10000) private String content; // 본문
    // @Column(nullable = false) : 엔티티 클래스의 특정 필드에 대한 데이터베이스 열 설정을 지정
        // `nullable = false`는 해당 열이 데이터베이스에 반드시 값을 가져야 함을 나타낸다. 따라서 이 필드는 null이 아니다.
        // `length = 10000`은 필드가 문자열인 경우 데이터베이스 열의 최대 길이를 나타낸다.

    @Setter private String hashtag; // 해시태그

    @ToString.Exclude
    //`toString()` 메서드가 자동으로 생성될 때 해당 필드를 제외하도록 지시
    // 이는 객체의 문자열 표현을 생성하 때 무한 루프를 방지하기 위해 주로 사용
    @OrderBy("createdAt DESC")
    //일대다 관계에서 많은 쪽 엔티티의 컬렉션을 정렬하는 방법을 지정
    //`createdAt` 열을 기준으로 내림차순으로 정렬
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    //일대다 관계를 설정
    //`mappedBy` 연관관계의 주인이 되는 엔티티의 필드를 지정
    //`article`필드를 통해 연관관계를 유지
    //`cascade = CascadeType.ALL`은 모든 종류의 연관된 변경이 부모 엔티티에 전파됩니다.
    private final Set<ArticleComment> articleComments
    //이 코드는 `articleComments`라는 이름의 필드를 선언
    //부모 엔티티가 `Article`이며, 자식 엔티티가 `ArticleComment`
            = new LinkedHashSet<>();
    //`articleComments` 필드를 초기화
    // 이 필드는 `LinckedHashSet`으로 초기화되며, 순서가 유지되는 Set



    protected Article() {}

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
        if (this == o) return true; // 객체가 자기 자신과 같은지 확인, 만약 같다면 `true` 를 반환
        if (!(o instanceof Article that)) return false;
        // 비교하려는 객체 `o`가 `Article` 클래스의 인스턴스인지 확인
        // 그렇지 않다면 `false`를 반환합니다. 동시에 `that` 변수에 캐스팅된 객체를 할당
        return this.getId() != null && this.getId().equals(that.getId());
        // 객체의 기본 키(`id`)가 `null`이 아니고,
        // 두 객체의 기본 키가 서로 같은지 확인
        // 두 객체의 기본 키가 동일하면 `true`를 반환하고, 그렇지 않으면 `false`를 반환
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
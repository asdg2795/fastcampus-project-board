package com.fastcampus.projectboard.repository;
import com.fastcampus.projectboard.domain.Article;
import com.fastcampus.projectboard.domain.QArticle;
import com.fastcampus.projectboard.repository.querydsl.ArticleRepositoryCustom;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
// Repository를 REST 엔드포인트로 노출하도록 지시하는 데 사용
// 해당 Repository에서 사용 가능한 CRUD(Create, Read, Update, Delete)
// 작업을 자동으로 처리하는 RESTful 엔드포인트를 생성
public interface ArticleRepository extends
        JpaRepository<Article, Long>,
        // Spring Data JPA에서 제공하는 기본적인 CRUD 작업을 처리하는 인터페이스
        // `Article` 엔티티와 그 엔티티의 식별자인 Long 타입을 파라미터로 받습니다.
        ArticleRepositoryCustom,
        // 사용자 지정 Repository 메서드를 제공하기 위한 사용자 정의 인터페이스 입니다.
        QuerydslPredicateExecutor<Article>,
        // QueryDSL을 사용하여 동적 쿼리를 생성하고 실행하는 인터페이스
        QuerydslBinderCustomizer<QArticle> {
        // QueryDSL 바인딩을 사용하여 쿼리 필드를 사용자 정의하는 데 필요한 인터페이스입니다.
    Page<Article> findByTitleContaining(String title, Pageable pageable);
    Page<Article> findByContentContaining(String content, Pageable pageable);
    Page<Article> findByUserAccount_UserIdContaining(String userId, Pageable pageable);
    Page<Article> findByUserAccount_NicknameContaining(String nickname, Pageable pageable);
    Page<Article> findByHashtag(String hashtag, Pageable pageable);

    void deleteByIdAndUserAccount_UserId(Long articleId, String userid);

    @Override
    default void customize(QuerydslBindings bindings, QArticle root) {
        bindings.excludeUnlistedProperties(true);
        // 바인딩에 명시적으로 지정되지 않은 속성을 제외합니다.
        // 이는 `root` 객체에 정의되지 않은 속성들은 쿼리 바인딩에 포함하지 않도록 합니다.
        bindings.including(root.title, root.content, root.hashtag, root.createdAt, root.createdBy);
        // 바인딩할 속성들을 명시적으로 지정합니다.
        bindings.bind(root.title).first(StringExpression::containsIgnoreCase);
        // 제목(title) 속성에 대한 바인딩 방법을 지정합니다.
        // 여기서는 대소문자를 구분하지 않고 부분 일치하는 검색을 수행합니다.
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase);
        // 내용(content) 속성에 대한 바인딩 방법을 지정합니다.
        // 제목과 마찬가지로 대소문자를 구분하지 않고 부분 일치하는 검색을 수행합니다.
        bindings.bind(root.hashtag).first(StringExpression::containsIgnoreCase);
        // 해시태그(hashtag) 속성에 대한 바인딩 방법을 지정합니다.
        // 제목과 마찬가지로 대소문자를 구분하지 않고 부분 일치하는 검색을 수행합니다.
        bindings.bind(root.createdAt).first(DateTimeExpression::eq);
        // 생성일시(createdAt) 속성에 대한 바인딩 방법을 지정합니다.
        // 여기서는 정확한 일치하는 검색을 수행합니다.
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);
        // 생성자(createdBy) 속성에 대한 바인딩 방법을 지정합니다.
        // 제목과 마찬가지로 대소문자를 구분하지 않고 부분 일치하는 검색을 수행합니다.
    }
}
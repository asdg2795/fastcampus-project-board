package com.fastcampus.projectboard.service;
import com.fastcampus.projectboard.domain.Article;
import com.fastcampus.projectboard.domain.UserAccount;
import com.fastcampus.projectboard.domain.constant.SearchType;
import com.fastcampus.projectboard.dto.ArticleDto;
import com.fastcampus.projectboard.dto.ArticleWithCommentsDto;
import com.fastcampus.projectboard.repository.ArticleRepository;
import com.fastcampus.projectboard.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Slf4j
// 로깅을 위한 롬복 어노테이션
@RequiredArgsConstructor
@Transactional
// 트랜잭션 처리를 위해 사용
// 메서드 내에서 실행되는 모든 작업이 하나의 트랜잭션 내에서 실행
// 메서드가 정상적으로 완료되거나 예외가 발생할 때 롤백될 수 있습니다.
@Service
// 해당 클래스가 비즈니스 로직을 담당하는 서비스 클래스임을 표시
// 해당 클래스를 스프링의 컴포넌트 스캔의 대상으로 지정
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final UserAccountRepository userAccountRepository;
    @Transactional(readOnly = true)
    // 읽기 전용 트랜잭션으로 설정
    // 이는 메서드 내에서 데이터를 변경하지 않고 읽기만 하는 작업을 수행할 때 성능을 최적화하는 데 도움이 된다.
    public Page<ArticleDto> searchArticles(SearchType searchType, String searchKeyword, Pageable pageable) {
        if (searchKeyword == null || searchKeyword.isBlank()) {
            return articleRepository.findAll(pageable).map(ArticleDto::from);
        }
        // 검색을 수행하는 메서드는 주어진 검색 유형(`searchType`) 및 검색 키워드(`searchKeyword`)에
        // 따라 다양한 방식으로 게시글을 검색
        // 검색 키워드가 null이거나 공백인 경우 모든 게시글을 반환
        // 그렇지 않으면 해당 검색 유형에 따라 검색을 수행
        return switch (searchType) {
            case TITLE -> articleRepository.findByTitleContaining(searchKeyword, pageable).map(ArticleDto::from);
            case CONTENT -> articleRepository.findByContentContaining(searchKeyword, pageable).map(ArticleDto::from);
            case ID -> articleRepository.findByUserAccount_UserIdContaining(searchKeyword, pageable).map(ArticleDto::from);
            case NICKNAME -> articleRepository.findByUserAccount_NicknameContaining(searchKeyword, pageable).map(ArticleDto::from);
            case HASHTAG -> articleRepository.findByHashtag("#" + searchKeyword, pageable).map(ArticleDto::from);
        };
    }

    @Transactional(readOnly = true)
    public ArticleWithCommentsDto getArticleWithComments(Long articleId) {

        return articleRepository.findById(articleId)
                // 주어진 `articleId`를 사용하여 게시글을 검색
                // 결과는 Optional<Article> 형식으로 반환
                .map(ArticleWithCommentsDto::from)
                // 게시글이 존재하는 경우에는 해당 게시글과 그에 대한 댓글 정보를 포함하는
                // `ArticleWithCommentsDto` 객체로 변환
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다 - articleId: " + articleId));
                // 게시글이 존재하지 않는 경우에는
                // `EntityNotFoundException`을 발생시킵니다.
                // 이는 해당 `articleId`에 해당하는 게시글이 없는 경우 예외를 발생시키고 이를 처리할 수 있도록 합니다.
    }

    @Transactional(readOnly = true)
    public ArticleDto getArticle(Long articleId) {
        return articleRepository.findById(articleId)
                .map(ArticleDto::from)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다 - articleId: " + articleId));
    }

    public void saveArticle(ArticleDto dto) {
        UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());
        // 주어진 `dto`에서 사용자 ID를 이용하여 사용자 계정 정보를 가져옵니다
        // `getReferenceById()` 메서드를 사용하여 실제로 데이터베이스에서 사용자 계정을 가져오는 것이 아니라,
        // 해당 사용자 계정의 프록시(Proxy)를 생성하여 반환
        // 이는 사용자 계정이 이미 영속 상태에 있거나 혹은 실제 사용자 계정 정보가 필요하지 않은 경우에 사용
        articleRepository.save(dto.toEntity(userAccount));
        // 변환된 게시글 엔티티를 `articleRepository`를 통해 저장

        // `dto.toEntity(userAccount)` : `dto` 객체에서 게시글 엔티티(`Article`)로 변환
        // 이때 `userAccount`를 작성자로 설정하여 게시글 엔티티를 생성
    }

    public void updateArticle(Long articleId, ArticleDto dto) {
        try {
            Article article = articleRepository.getReferenceById(articleId);
            UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());

            if (article.getUserAccount().equals(userAccount)) {
                // 게시글의 작성자와 주어진 사용자 계정이 일치하는지 확인
                if (dto.title() != null) { article.setTitle(dto.title()); }
                // 주어진 DTO의 제목이 null이 아닌 경우에만, 게시글의 제목을 주어진 DTO의 제목으로 업데이트
                if (dto.content() != null) { article.setContent(dto.content()); }
                // 주어진 DTO의 내용이 null이 아닌 경우에만, 게시글의 내용을 주어진 DTO의 내용으로 업데이트
                article.setHashtag(dto.hashtag());
                // 게시글의 해시태그를 주어진 DTO의 해시태그로 업데이트
            }
        } catch (EntityNotFoundException e) {
            log.warn("게시글 업데이트 실패. 게시글을 수정하는데 필요한 정보를 찾을 수 없습니다 - {}", e.getLocalizedMessage());
        }
    }

    public void deleteArticle(long articleId, String userId) {
        articleRepository.deleteByIdAndUserAccount_UserId(articleId, userId);
    }

    public long getArticleCount() {
        return articleRepository.count();
    }
    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticlesViaHashtag(String hashtag, Pageable pageable) {
        if (hashtag == null || hashtag.isBlank()) {
            // 주어진 해시태그가 null이거나 공백인 경우,
            // 빈 페이지를 반환
            // 이는 잘못된 입력을 처리하고 빈 결과를 반환하는 것입니다.
            return Page.empty(pageable);
        }
        return articleRepository.findByHashtag(hashtag, pageable)
                // 주어진 해시태그를 사용하여 게시글을 검색
                // 이는 Spring Data JAP의 메서드 네임 컨벤션에 따라 해당 해시태그를 포함하는 게시글을 찾는 것
                // 검색 결과는 `Page<Article>` 형식으로 반환됩니다.
                .map(ArticleDto::from);
                // 검색 결과인 `Page<Article>`를 `ArticleDto`로 변환하여 반환합니다.
                // 이는 각 게시글 엔티티를 해당 DTO로 변환하는 것
    }

    public List<String> getHashtags() {
        return articleRepository.findAllDistinctHashtags();
    }
}
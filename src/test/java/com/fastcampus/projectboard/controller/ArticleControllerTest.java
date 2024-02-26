package com.fastcampus.projectboard.controller;

import com.fastcampus.projectboard.config.TestSecurityConfig;
import com.fastcampus.projectboard.domain.constant.FormStatus;
import com.fastcampus.projectboard.domain.constant.SearchType;
import com.fastcampus.projectboard.dto.ArticleDto;
import com.fastcampus.projectboard.dto.ArticleWithCommentsDto;
import com.fastcampus.projectboard.dto.UserAccountDto;
import com.fastcampus.projectboard.dto.request.ArticleRequest;
import com.fastcampus.projectboard.dto.response.ArticleResponse;
import com.fastcampus.projectboard.service.ArticleService;
import com.fastcampus.projectboard.service.PaginationService;
import com.fastcampus.projectboard.util.FormDataEncoder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View 컨트롤러 - 게시글")
@Import({TestSecurityConfig.class, FormDataEncoder.class})
//Spring Framework에서 다른 설정 클래스를 가져오는 데 사용
@WebMvcTest(ArticleController.class)
//Spring Boot 애플리케이션의 테스트에서 사용되는 어노테이션
class ArticleControllerTest {

    private final MockMvc mvc;
    //MockMvc : 테스트를 작성하면 실제 HTTP 요청을 보내는 대신에 Spring MVC 애플리케이션 내부에서
    //요청을 처리하는 것처럼 시뮬레이션 할 수 있습니다.
    private final FormDataEncoder formDataEncoder;
    @MockBean private ArticleService articleService;
    @MockBean private PaginationService paginationService;
    //테스트 컨텍스트에 의해 관리되는 빈 선언되며, 해당 빈은 목 객체(Mock Object)로 초기화됩니다.
    //목 객체는 실제 객체처럼 동작하지만,
    //프로그래머가 직접 정의한 행동을 수행하며,
    //테스트의 목적에 따라 다르게 구성될 수 있습니다.
    public ArticleControllerTest(
            @Autowired MockMvc mvc,
            @Autowired FormDataEncoder formDataEncoder
            //`@Autowired`를 사용하는 이유
            // 코드의 가독성 및 유연성을 높이기 위함, 개발자가 빈을 명시적으로 주입하지 않고 Spring이 자동으로 의존성을 관리하게 하므로 코드가 간결해짐

    ) {
        this.mvc = mvc;
        this.formDataEncoder = formDataEncoder;
    }
    @DisplayName("[view][GET] 게시글 리스트 (게시판) 페이지 - 정상 호출")
    @Test
    public void givenNothing_whenRequestingArticlesView_thenReturnsArticlesView() throws Exception {
        // Given
        given(articleService.searchArticles(eq(null), eq(null), any(Pageable.class))).willReturn(Page.empty());
        //`articleService.searchArticles()` :
        // 메서드에 null 값을 인자로 전달할 때, `Page.empty()`를 반환하도록 설정
        // `searchArticles()` 메서드가 특정 인자 값이 `null`인 경우에 대한 동작을 정의
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0, 1, 2, 3, 4));
        //`paginationService.getPaginationBarNumbers()`
        // 메서드에 아무 정수값이나 인자로 전달 할 때, `[0, 1, 2, 3, 4]` 리스트로 반환하도록 서렂ㅇ
        // When & Then
        mvc.perform(get("/articles"))
                .andExpect(status().isOk())
                // HTTP 응답 상태 코드가 200(OK)인지 확인
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                // 응답의 컨텐츠 타입이 `text/html`인지 확인
                .andExpect(view().name("articles/index"))
                // 렌더링된 뷰의 이름이 "article/index"인지 확인
                .andExpect(model().attributeExists("articles"))
                // 모델에 "articles" 속성이 존재하는지 확인
                .andExpect(model().attributeExists("paginationBarNumbers"))
                // 모델에 "paginationBarNumbers" 속성이 존재하는지 확인
                .andExpect(model().attributeExists("searchTypes"));
                // 모델에 "searchTypes" 속성이 존재하는지 확인
        then(articleService).should().searchArticles(eq(null), eq(null), any(Pageable.class));
        // `articleService`에 대해 `searchArticles` 메서드가 `null` 값을 인자로 호출되었는지를 검증
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
        // `paginationService`에 대해 `getPaginationBarNumbers` 메서드가 아무 정수 값을 인자로 호출 되었는지를 검증
    }
    @DisplayName("[view][GET] 게시글 리스트 (게시판) 페이지 - 검색어와 함께 호출")
    @Test
    public void givenSearchKeyword_whenSearchingArticlesView_thenReturnsArticlesView() throws Exception {
        // Given
        SearchType searchType = SearchType.TITLE;
        String searchValue = "title";
        given(articleService.searchArticles(eq(searchType), eq(searchValue), any(Pageable.class))).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0, 1, 2, 3, 4));
        // When & Then
        mvc.perform(
                        get("/articles")
                                .queryParam("searchType", searchType.name())
                                .queryParam("searchValue", searchValue)
                //`queryParam()` 메서드를 사용하여 검색 유형(`searchType`)과 검색 값(`searchValue`)을 쿼리 파라미터로 추가
                //`searchType.name()`은 `searchType` 열거형의 이름을 문자열로 변환하여 쿼리 파라미터로 전달
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attributeExists("searchTypes"));
        then(articleService).should().searchArticles(eq(searchType), eq(searchValue), any(Pageable.class));
        //`articleService`의 `searchArticles` 메서드가 `searchType`과 `searchValue`를 인자로 호출되었는지를 검증
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
        //`paginationService`의 `getPaginationBarNumbers` 메서드가 아무 정수값을 인자로 호출되었는지를 검증
    }
    @DisplayName("[view][GET] 게시글 리스트 (게시판) 페이지 - 페이징, 정렬 기능")
    @Test
    void givenPagingAndSortingParams_whenSearchingArticlesView_thenReturnsArticlesView() throws Exception {
        // Given
        String sortName = "title";
        String direction = "desc";
        int pageNumber = 0;
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc(sortName)));
        List<Integer> barNumbers = List.of(1, 2, 3, 4, 5);
        given(articleService.searchArticles(null, null, pageable)).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(pageable.getPageNumber(), Page.empty().getTotalPages())).willReturn(barNumbers);
        // When & Then
        mvc.perform(
                get("/articles")
                        .queryParam("page", String.valueOf(pageNumber))
                        .queryParam("size", String.valueOf(pageSize))
                        .queryParam("sort", sortName + "," + direction)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attribute("paginationBarNumbers", barNumbers));
                //`paginationBarNumbres` 라는 모델 속성이 있고, 그 값이 `barNumbers`와 동일한지를 확인합니다.
        then(articleService).should().searchArticles(null, null, pageable);
        then(paginationService).should().getPaginationBarNumbers(pageable.getPageNumber(), Page.empty().getTotalPages());
    }

    @DisplayName("[view][GET] 게시글 페이지 - 인증 없을 땐 로그인 페이지로 이동")
    @Test
    void givenNothing_whenRequestingArticlePage_thenRedirectsToLoginPage() throws Exception {
        // Given
        long articleId = 1L;

        // When & Then
        mvc.perform(get("/articles/" + articleId))
                .andExpect(status().is3xxRedirection())
                //응답의 상태 코드가 3xx 리다이렉션인지를 확인
                .andExpect(redirectedUrlPattern("**/login"));
                //리다이렉션된 URL이 "**/login" 패턴을 따르는지를 확인
        then(articleService).shouldHaveNoInteractions();
        then(articleService).shouldHaveNoInteractions();
        //`articleService`와의 상호작용이 없었음을 확인
    }

    @WithMockUser
    //특정 사용자로부터 인증된 상태를 시뮬레이션할 때 사용됩니다.
    //보안 관련 로직을 테스트 할 때 유용합니다.
    @DisplayName("[view][GET] 게시글 페이지 - 정상 호출, 인증된 사용자")
    @Test
    public void givenNothing_whenRequestingArticleView_thenReturnsArticleView() throws Exception {
        // Given
        Long articleId = 1L;
        long totalCount = 1L;
        given(articleService.getArticleWithComments(articleId)).willReturn(createArticleWithCommentsDto());
        given(articleService.getArticleCount()).willReturn(totalCount);
        // When & Then
        mvc.perform(get("/articles/" + articleId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/detail"))
                .andExpect(model().attributeExists("article"))
                // 모델에 "article" 속성이 존재하는지 확인합니다.
                .andExpect(model().attributeExists("articleComments"))
                .andExpect(model().attributeExists("articleComments"))
                .andExpect(model().attribute("totalCount", totalCount));
        then(articleService).should().getArticleWithComments(articleId);
        then(articleService).should().getArticleCount();
        //게시물의 총 수를 가져오는 것
    }
    @Disabled("구현 중")
    @DisplayName("[view][GET] 게시글 검색 전용 페이지 - 정상 호출")
    @Test
    public void givenNothing_whenRequestingArticleSearchView_thenReturnsArticleSearchView() throws Exception {
        // Given
        // When & Then
        mvc.perform(get("/articles/search"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/search"));
    }
    @DisplayName("[view][GET] 게시글 해시태그 검색 페이지 - 정상 호출")
    @Test
    public void givenNothing_whenRequestingArticleSearchHashtagView_thenReturnsArticleSearchHashtagView() throws Exception {
        // Given
        List<String> hashtags = List.of("#java", "#spring", "#boot");
        given(articleService.searchArticlesViaHashtag(eq(null), any(Pageable.class))).willReturn(Page.empty());
        given(articleService.getHashtags()).willReturn(hashtags);
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(1, 2, 3, 4, 5));
        // When & Then
        mvc.perform(get("/articles/search-hashtag"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/search-hashtag"))
                .andExpect(model().attribute("articles", Page.empty()))
                .andExpect(model().attribute("hashtags", hashtags))
                .andExpect(model().attributeExists("paginationBarNumbers"))
                .andExpect(model().attribute("searchType", SearchType.HASHTAG));
        then(articleService).should().searchArticlesViaHashtag(eq(null), any(Pageable.class));
        then(articleService).should().getHashtags();
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }
    @DisplayName("[view][GET] 게시글 해시태그 검색 페이지 - 정상 호출, 해시태그 입력")
    @Test
    public void givenHashtag_whenRequestingArticleSearchHashtagView_thenReturnsArticleSearchHashtagView() throws Exception {
        // Given
        String hashtag = "#java";
        List<String> hashtags = List.of("#java", "#spring", "#boot");
        given(articleService.searchArticlesViaHashtag(eq(hashtag), any(Pageable.class))).willReturn(Page.empty());
        given(articleService.getHashtags()).willReturn(hashtags);
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(1, 2, 3, 4, 5));
        // When & Then
        mvc.perform(
                        get("/articles/search-hashtag")
                                .queryParam("searchValue", hashtag)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/search-hashtag"))
                .andExpect(model().attribute("articles", Page.empty()))
                .andExpect(model().attribute("hashtags", hashtags))
                .andExpect(model().attributeExists("paginationBarNumbers"))
                .andExpect(model().attribute("searchType", SearchType.HASHTAG));
        then(articleService).should().searchArticlesViaHashtag(eq(hashtag), any(Pageable.class));
        then(articleService).should().getHashtags();
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }

    @WithMockUser
    @DisplayName("[view][GET] 새 게시글 작성 페이지")
    @Test
    void givenNothing_whenRequesting_thenReturnsNewArticlePage() throws Exception {
        // Given
        // When & Then
        mvc.perform(get("/articles/form"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/form"))
                .andExpect(model().attribute("formStatus", FormStatus.CREATE));
    }

    @WithUserDetails(value = "unoTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] 새 게시글 등록 - 정상 호출")
    @Test
    void givenNewArticleInfo_whenRequesting_thenSavesNewArticle() throws Exception {
        // Given
        ArticleRequest articleRequest = ArticleRequest.of("new title", "new content", "#new");
        willDoNothing().given(articleService).saveArticle(any(ArticleDto.class));
        // When & Then
        mvc.perform(
                        post("/articles/form")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                //요청의 컨텐츠 타입이 `application/x-www-form-urlencoded`임을 설정
                                .content(formDataEncoder.encode(articleRequest))
                                //폼 데이터를 인코딩하여 요청 본문에 포함
                                .with(csrf())
                                //Cross-Site Request Forgery(CSRF) 방지를 위채 CSRF 토큰을 요청에 포함
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles"))
                .andExpect(redirectedUrl("/articles"));
                //리다이렉트된 URL이 "/articles"임을 확인
        then(articleService).should().saveArticle(any(ArticleDto.class));
    }

    @DisplayName("[view][GET] 게시글 수정 페이지 - 인증 없을 땐 로그인 페이지로 이동")
    @Test
    void givenNothing_whenRequesting_thenRedirectsToLoginPage() throws Exception {
        // Given
        long articleId = 1L;

        // When & Then
        mvc.perform(get("/articles/" + articleId + "/form"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
        then(articleService).shouldHaveNoInteractions();
    }

    @WithMockUser
    @DisplayName("[view][GET] 게시글 수정 페이지 - 정상 호출, 인증된 사용자")
    @Test
    void givenNothing_whenRequesting_thenReturnsUpdatedArticlePage() throws Exception {
        // Given
        long articleId = 1L;
        ArticleDto dto = createArticleDto();
        given(articleService.getArticle(articleId)).willReturn(dto);
        // When & Then
        mvc.perform(get("/articles/" + articleId + "/form"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/form"))
                .andExpect(model().attribute("article", ArticleResponse.from(dto)))
                .andExpect(model().attribute("formStatus", FormStatus.UPDATE));
        then(articleService).should().getArticle(articleId);
    }

    @WithUserDetails(value = "unoTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    //`@WithUserDetails` : 주로 인증된 사용자의 역할을 시뮬레이션하고 보안 관련 테스트를 수행할 때 사용
    @DisplayName("[view][POST] 게시글 수정 - 정상 호출")
    @Test
    void givenUpdatedArticleInfo_whenRequesting_thenUpdatesNewArticle() throws Exception {
        // Given
        long articleId = 1L;
        ArticleRequest articleRequest = ArticleRequest.of("new title", "new content", "#new");
        willDoNothing().given(articleService).updateArticle(eq(articleId), any(ArticleDto.class));
        // When & Then
        mvc.perform(
                        post("/articles/" + articleId + "/form")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(articleRequest))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles/" + articleId))
                .andExpect(redirectedUrl("/articles/" + articleId));
        then(articleService).should().updateArticle(eq(articleId), any(ArticleDto.class));
    }

    @WithUserDetails(value = "unoTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] 게시글 삭제 - 정상 호출")
    @Test
    void givenArticleIdToDelete_whenRequesting_thenDeletesArticle() throws Exception {
        // Given
        long articleId = 1L;
        String userId = "unoTest";
        willDoNothing().given(articleService).deleteArticle(articleId, userId);

        // When & Then
        mvc.perform(
                        post("/articles/" + articleId + "/delete")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles"))
                .andExpect(redirectedUrl("/articles"));
        then(articleService).should().deleteArticle(articleId, userId);
    }


    private ArticleDto createArticleDto() {
        return ArticleDto.of(
                createUserAccountDto(),
                "title",
                "content",
                "#java"
        );
    }
    private ArticleWithCommentsDto createArticleWithCommentsDto() {
        return ArticleWithCommentsDto.of(
                1L,
                createUserAccountDto(),
                Set.of(),
                "title",
                "content",
                "#java",
                LocalDateTime.now(),
                "uno",
                LocalDateTime.now(),
                "uno"
        );
    }
    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                "uno",
                "pw",
                "uno@mail.com",
                "Uno",
                "memo",
                LocalDateTime.now(),
                "uno",
                LocalDateTime.now(),
                "uno"
        );
    }
}
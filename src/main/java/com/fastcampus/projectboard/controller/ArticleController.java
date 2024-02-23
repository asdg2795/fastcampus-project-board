package com.fastcampus.projectboard.controller;
import com.fastcampus.projectboard.domain.constant.FormStatus;
import com.fastcampus.projectboard.domain.constant.SearchType;
import com.fastcampus.projectboard.dto.request.ArticleRequest;
import com.fastcampus.projectboard.dto.response.ArticleResponse;
import com.fastcampus.projectboard.dto.response.ArticleWithCommentsResponse;
import com.fastcampus.projectboard.dto.security.BoardPrincipal;
import com.fastcampus.projectboard.service.ArticleService;
import com.fastcampus.projectboard.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequiredArgsConstructor
// 클래스의 모든 final 필드에 대한 생성자를 생성한다.
// `articleService` 및 `paginatonService` 필드에 대한 주입이 자동으로 이루어짐
@RequestMapping("/articles")
@Controller
public class ArticleController {
    private final ArticleService articleService;
    private final PaginationService paginationService;
    @GetMapping
    public String articles(
            @RequestParam(required = false) SearchType searchType,
            @RequestParam(required = false) String searchValue,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            // 페이지네이션을 처리하는 데 사용된다
            // `@PageableDefault` : 페이지네이션의 기본 설정을 지정합니다.(페이지 크기는 10, `createdAt`필드는 기준으로 내림차순으로 정렬)
            ModelMap map
            // 이 객체를 사용하여 컨트롤러 메서드에서 뷰로 데이터를 전달

            // 이 메서드는 요청 파라미터로부터 검색조건(`searchType`, `searchValue`)과
            // 페이지네이션 정보를 ('pageable`)를 받아와서 해당 검색 조건에 맞는 글 목록을 조회하고
            // 이를 뷰에 전달하기 위해 `ModelMap`에 데이터를 추가
    ) {
        Page<ArticleResponse> articles = articleService.searchArticles(searchType, searchValue, pageable).map(ArticleResponse::from);
        // `articleService`를 사용하여 요청된 검색 조건에 따라 글를 검색
        // `searchArticles` 메서드는 검색 조건에 따라 페이지네이션된 글 목록을 반환
        // 그 후, `map` 메서드를 사용하여 각 글를 `ArticleResponse` 객체로 변환
        // `ArticleResponse::from`은 `ArticleResponse` 객체를 생성하기 위한 정적 메서드 또는 생성자를 가리킨다.
        // 결과는 `Page<ArticleResponse>` 객체인 `articles`에 저장
        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articles.getTotalPages());
        // 이 코드는 페이지네이션 바에 표시할 페이지 번호 목록을 가져옵니다.
        // `paginationService`의 `getPaginationBarNumbers` 메서드는 현재 페이지 번호와 전체 페이지 수를 인수로 받아서 페이지 번호 목록을 생성
        // `barNumbers`에 저장
        map.addAttribute("articles", articles);
        map.addAttribute("paginationBarNumbers", barNumbers);
        map.addAttribute("searchTypes", SearchType.values());
        // 이 코드들은 뷰로 데이터를 전달하기 위해 `ModelMap` 객체에 데이터를 추가
        // `articles`, `paginationBarNumbers`, `searchTypes`라는 이름으로
        // 각각 글 목록, 페이지네이션 바 번호 목록, 검색 유형 목록을 추가
        return "articles/index";
    }
    @GetMapping("/{articleId}")
    public String article(@PathVariable Long articleId, ModelMap map) {
        //`@PathVariable Long articleId` : 경로 변수인 `{articleId}` 를 메서드의 매개변수로 받아옵니다.
        // 이 경로 변수는 URL에서 추출된 특정 글의 ID를 나타낸다.
        // `ModelMap map` :  이 객체를 사용하여 뷰로 데이터를 전달할 수 있습니다.
        ArticleWithCommentsResponse article =
        //`articleService`를 사용하여 특정 글의 세부 정보와 댓글을 가져온 후,
        // 이를 `ArticleWithCommentResponse` 객체로 변환        
                ArticleWithCommentsResponse.from(
                // `ArticleWithCommentResponse.from(...)` : `ArticleWithCommentsResponse 클래스의
                // `from` 정적 메서드를 사용하여 `articleService`에서 가져온 응답을
                // `ArticleWithCommentsResponse 객체로 변환한다.
                // 이 메서드는 가져온 데이터를 적절히 가공하여 `ArticleWithCommentsResponse` 객체를 생성
                        articleService.getArticleWithComments(articleId));
                        // `articleService`의 `getArticleWithComments` 메서드를 호출하여 특정 글의 세부 정보와 댓글을 가져온다
                        // 이 메서드의 특정 글의 ID를 인수로 받아와 해당 글의 세부 정보와 관련된 댓글을 포함한 응답을 반환ㄱ

        // 결과적으로 `article` 변수에는 특정 글와 해당 글의 댓글이 포함된
        //`ArticleWithCommentsResponse`객체가 할당된다.
        // 이 객체는 이후에 뷰로 전달되어 클라이언트에 응답되는데,
        // 클라이언트는 이 데이터를 사용하여 웹 페이지를 동적으로 렌더링 할 수 있다.
        map.addAttribute("article", article);
        map.addAttribute("articleComments", article.articleCommentsResponse());
        map.addAttribute("totalCount", articleService.getArticleCount());
        return "articles/detail";
    }
    @GetMapping("/search-hashtag")
    public String searchArticleHashtag(
            @RequestParam(required = false) String searchValue,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            ModelMap map
    ) {
        Page<ArticleResponse> articles = articleService.searchArticlesViaHashtag(searchValue, pageable).map(ArticleResponse::from);
        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articles.getTotalPages());
        List<String> hashtags = articleService.getHashtags();
        map.addAttribute("articles", articles);
        map.addAttribute("hashtags", hashtags);
        map.addAttribute("paginationBarNumbers", barNumbers);
        map.addAttribute("searchType", SearchType.HASHTAG);
        return "articles/search-hashtag";
    }
    @GetMapping("/form")
    public String articleForm(ModelMap map) {
        map.addAttribute("formStatus", FormStatus.CREATE);
        return "articles/form";
    }

    @PostMapping ("/form")
    public String postNewArticle(
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            // 이 코드는 현재 인증된 사용자를 나타내는 `BoardPrincipal` 객체를 메서드의 매개변수로 받아온다.
            // `@AuthenicationPrincipal` : 현재 인증된 사용자를 추출하는 데 사용
            ArticleRequest articleRequest
            // HTTP 요청 바디에서 전송된 데이터를 바인딩하기 위한 `ArticleRequest` 객체를 메서드의 매개변수로 받아온다.
            // 이 객체는 새로운 글의 정보를 포함한다.
    ) {
        articleService.saveArticle(articleRequest.toDto(boardPrincipal.toDto()));
        // `articleService` 를 사용하여 새로운 글을 저장
        // `toDto()` 메서드를 사용하여 `ArticleRequest` 객체를 DTO(Domain Transfer Object)로 변환한 후,
        // `boardprincipal.toDto()`를 사용하여 `BoardPrincipal` 객체를 DTO로 변환
        // 이렇게 변환된 DTO를 사용하여 새로운 글을 저장

        return "redirect:/articles";
    }
    @GetMapping("/{articleId}/form")
    public String updateArticleForm(@PathVariable Long articleId, ModelMap map) {
        ArticleResponse article = ArticleResponse.from(articleService.getArticle(articleId));
        map.addAttribute("article", article);
        map.addAttribute("formStatus", FormStatus.UPDATE);
        return "articles/form";
    }

    @PostMapping ("/{articleId}/form")
    public String updateArticle(
            @PathVariable Long articleId,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            ArticleRequest articleRequest
    ) {
        articleService.updateArticle(articleId, articleRequest.toDto(boardPrincipal.toDto()));

        return "redirect:/articles/" + articleId;
    }

    @PostMapping ("/{articleId}/delete")
    public String deleteArticle(
            @PathVariable Long articleId,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        articleService.deleteArticle(articleId, boardPrincipal.getUsername());
        // 이 코드는 `articleService`의 `deleteArticle` 메서드를 호출
        // 이 메서드는 특정 글을 삭제하는데, 인자로는 삭제할 글의 ID(`articleId`)와
        // 요청을 보낸 사용자의 이름 (`boardPrincipal.getUsername()`)을 받습니다.
        // 이를 통해 해당 글을 삭제할 권한이 있는지 확인하고, 권한이 있을 경우에만 글을 삭제

        return "redirect:/articles";
    }
}
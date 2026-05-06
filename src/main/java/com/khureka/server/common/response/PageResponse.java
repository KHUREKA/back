package com.khureka.server.common.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 페이징 목록 응답을 위한 공통 DTO.
 *
 * 사용 예시:
 *
 * Page<PostResponse> page = postService.getPosts(pageable);
 * return ResponseEntity.ok(ApiResponse.success(PageResponse.from(page)));
 *
 * 최종 응답 예시:
 * {
 *   "result": "SUCCESS",
 *   "data": {
 *     "content": [ ... ],
 *     "page": 0,
 *     "size": 10,
 *     "totalElements": 32,
 *     "totalPages": 4,
 *     "numberOfElements": 10,
 *     "first": true,
 *     "last": false,
 *     "empty": false
 *   }
 * }
 */
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class PageResponse<T> {

    /**
     * 현재 페이지의 실제 데이터 목록.
     */
    private final List<T> content;

    /**
     * 현재 페이지 번호.
     *
     * Spring Data Page는 0부터 시작한다.
     * 예: 첫 페이지 = 0
     */
    private final int page;

    /**
     * 요청한 페이지 크기.
     */
    private final int size;

    /**
     * 전체 데이터 개수.
     */
    private final long totalElements;

    /**
     * 전체 페이지 수.
     */
    private final int totalPages;

    /**
     * 현재 페이지에 실제로 담긴 데이터 개수.
     */
    private final int numberOfElements;

    /**
     * 첫 번째 페이지 여부.
     */
    private final boolean first;

    /**
     * 마지막 페이지 여부.
     */
    private final boolean last;

    /**
     * 현재 페이지가 비어 있는지 여부.
     */
    private final boolean empty;

    /**
     * Page<T>를 PageResponse<T>로 변환한다.
     *
     * Service에서 이미 Page<ResponseDto> 형태로 변환한 경우 사용한다.
     */
    public static <T> PageResponse<T> from(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .numberOfElements(page.getNumberOfElements())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }

    /**
     * Page<Entity>의 페이징 메타데이터와 별도로 변환된 DTO 목록을 조합한다.
     *
     * 예:
     * Page<Post> postPage = postRepository.findAll(pageable);
     * List<PostResponse> content = postPage.getContent()
     *         .stream()
     *         .map(PostResponse::from)
     *         .toList();
     *
     * return PageResponse.from(postPage, content);
     */
    public static <T> PageResponse<T> from(Page<?> page, List<T> content) {
        return PageResponse.<T>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .numberOfElements(page.getNumberOfElements())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }
}
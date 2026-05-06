# Coding Agent 공통 계층 사용 가이드

## 0. 목적

이 문서는 Codex, Claude Code 같은 코딩 에이전트가 KHUREKA 백엔드 프로젝트에서 코드를 생성하거나 수정할 때 반드시 따라야 할 공통 계층 사용 규칙을 정리한 문서다.

코딩 에이전트는 이 문서를 기준으로 다음 공통 계층을 이해하고 사용해야 한다.

```text
ApiResponse
ErrorResponse
ErrorCode
BusinessException
GlobalExceptionHandler
PageResponse
BaseEntity
JpaAuditingConfig
```

이 문서의 목표는 다음과 같다.

```text
공통 응답 형식 유지
공통 에러 처리 방식 유지
ErrorCode 기반 예외 처리 유지
페이징 응답 형식 유지
Entity 공통 시간 관리 방식 유지
기존 팀원이 작성한 Auth/Security 코드에 대한 불필요한 수정 방지
```

---

## 1. 프로젝트 기본 정보

현재 프로젝트의 base package는 다음과 같다.

```text
com.khureka.server
```

코딩 에이전트는 절대 다음 패키지를 새로 만들면 안 된다.

```text
com.example.myproject
com.example.demo
```

모든 새 파일은 반드시 `com.khureka.server` 하위에 생성한다.

---

## 2. 현재 공통 계층 파일 구조

현재 공통 계층은 다음 파일들을 기준으로 한다.

```text
src/main/java/com/khureka/server/common/response/ApiResponse.java
src/main/java/com/khureka/server/common/response/ErrorResponse.java
src/main/java/com/khureka/server/common/response/PageResponse.java

src/main/java/com/khureka/server/common/exception/ErrorCode.java
src/main/java/com/khureka/server/common/exception/BusinessException.java
src/main/java/com/khureka/server/common/exception/GlobalExceptionHandler.java

src/main/java/com/khureka/server/common/entity/BaseEntity.java
src/main/java/com/khureka/server/config/JpaAuditingConfig.java
```

관련 문서는 다음 위치에 있다.

```text
docs/API_RESPONSE_CONVENTION.md
docs/EXCEPTION_HANDLING_CONVENTION.md
docs/PAGING_RESPONSE_CONVENTION.md
docs/ENTITY_CONVENTION.md
docs/COMMON_LAYER_SUMMARY.md
```

---

## 3. 가장 중요한 원칙

코딩 에이전트는 코드를 생성할 때 아래 원칙을 반드시 지킨다.

```text
1. 성공 응답은 ApiResponse.success(...)로 감싼다.
2. 에러 응답은 직접 만들지 않고 BusinessException을 던진다.
3. 에러 코드는 ErrorCode에 먼저 정의한다.
4. Service에서는 ResponseEntity를 반환하지 않는다.
5. Controller에서는 성공 응답만 만든다.
6. Controller마다 try-catch를 만들지 않는다.
7. Entity를 API 응답으로 직접 반환하지 않는다.
8. 목록 응답은 PageResponse를 사용한다.
9. 새 Entity는 BaseEntity 상속을 우선 고려한다.
10. 기존 Auth/Security 코드는 명시적 요청 없이는 수정하지 않는다.
```

---

## 4. ApiResponse 사용 규칙

`ApiResponse`는 API 최상위 응답 래퍼다.

성공 응답은 반드시 `ApiResponse.success(...)`를 사용한다.

---

### 4.1 데이터가 있는 성공 응답

```java
@GetMapping("/{id}")
public ResponseEntity<ApiResponse<PostResponse>> getPost(@PathVariable Long id) {
    PostResponse response = postService.getPost(id);
    return ResponseEntity.ok(ApiResponse.success(response));
}
```

응답 예시:

```json
{
  "result": "SUCCESS",
  "data": {
    "id": 1,
    "title": "게시글 제목"
  }
}
```

---

### 4.2 데이터가 없는 성공 응답

```java
@PostMapping("/{id}/like")
public ResponseEntity<ApiResponse<Void>> likePost(@PathVariable Long id) {
    postService.likePost(id);
    return ResponseEntity.ok(ApiResponse.success());
}
```

응답 예시:

```json
{
  "result": "SUCCESS"
}
```

---

### 4.3 생성 성공 응답

새 리소스를 생성한 경우 HTTP 상태 코드는 `201 Created`를 사용한다.

```java
@PostMapping
public ResponseEntity<ApiResponse<PostResponse>> createPost(
        @Valid @RequestBody PostCreateRequest request
) {
    PostResponse response = postService.createPost(request);

    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success(response));
}
```

---

### 4.4 삭제 성공 응답

삭제 성공은 `204 No Content`를 사용한다.

이 경우 `ApiResponse.success()`를 사용하지 않고 body를 내려주지 않는다.

```java
@DeleteMapping("/{id}")
public ResponseEntity<Void> deletePost(@PathVariable Long id) {
    postService.deletePost(id);
    return ResponseEntity.noContent().build();
}
```

---

## 5. ErrorResponse 사용 규칙

`ErrorResponse`는 에러 상세 정보를 담는 객체다.

일반적으로 Controller나 Service에서 직접 생성하지 않는다.

에러 응답 생성은 `GlobalExceptionHandler`가 담당한다.

최종 에러 응답 형식은 다음과 같다.

```json
{
  "result": "ERROR",
  "error": {
    "code": "POST-404-001",
    "message": "게시글을 찾을 수 없습니다.",
    "timestamp": "2026-05-05T18:10:22.123+09:00",
    "path": "/api/v1/posts/1"
  }
}
```

`@Valid` 검증 실패 시에는 `errors` 필드가 포함될 수 있다.

```json
{
  "result": "ERROR",
  "error": {
    "code": "COMMON-400",
    "message": "제목은 필수입니다.",
    "timestamp": "2026-05-05T18:10:22.123+09:00",
    "path": "/api/v1/posts",
    "errors": [
      {
        "field": "title",
        "reason": "제목은 필수입니다."
      }
    ]
  }
}
```

---

## 6. ErrorCode 사용 규칙

모든 에러 상황은 `ErrorCode`에 먼저 정의한다.

에러 코드는 다음 형식을 따른다.

```text
공통 에러:
COMMON-{HTTP_STATUS}

도메인 에러:
{DOMAIN}-{HTTP_STATUS}-{NUMBER}
```

예시:

```java
INVALID_REQUEST("COMMON-400", 400, "잘못된 요청입니다."),
AUTH_INVALID_CREDENTIAL("AUTH-401-001", 401, "이메일 또는 비밀번호가 올바르지 않습니다."),
USER_NOT_FOUND("USER-404-001", 404, "사용자를 찾을 수 없습니다."),
POST_NOT_FOUND("POST-404-001", 404, "게시글을 찾을 수 없습니다.");
```

새 도메인 에러가 필요하면 다음처럼 추가한다.

```java
TEAM_NOT_FOUND("TEAM-404-001", 404, "팀을 찾을 수 없습니다."),
TEAM_ALREADY_JOINED("TEAM-409-001", 409, "이미 참여한 팀입니다."),
TEAM_JOIN_FORBIDDEN("TEAM-403-001", 403, "해당 팀에 참여할 권한이 없습니다.");
```

코딩 에이전트는 문자열 메시지를 직접 조립해 `RuntimeException`으로 던지면 안 된다.

금지:

```java
throw new RuntimeException("게시글을 찾을 수 없습니다.");
```

권장:

```java
throw new BusinessException(ErrorCode.POST_NOT_FOUND);
```

---

## 7. BusinessException 사용 규칙

Service에서 비즈니스 실패 상황이 발생하면 `BusinessException`을 던진다.

기본 사용:

```java
throw new BusinessException(ErrorCode.POST_NOT_FOUND);
```

상세 메시지가 필요한 경우:

```java
throw new BusinessException(
        ErrorCode.POST_NOT_FOUND,
        "ID " + postId + "에 해당하는 게시글을 찾을 수 없습니다."
);
```

원인 예외를 보존해야 하는 경우:

```java
try {
    // 외부 API 호출
} catch (Exception e) {
    throw new BusinessException(ErrorCode.INTERNAL_ERROR, e);
}
```

---

## 8. GlobalExceptionHandler 사용 흐름

코딩 에이전트는 Controller나 Service에 직접 에러 응답 생성 코드를 만들지 않는다.

예외 처리 흐름은 다음과 같다.

```text
Service에서 BusinessException 발생
→ Controller 밖으로 예외 전파
→ GlobalExceptionHandler가 예외 처리
→ ErrorCode 기반 ErrorResponse 생성
→ ApiResponse.error(...)로 최종 응답 반환
```

따라서 Service에서는 다음처럼만 작성한다.

```java
Post post = postRepository.findById(postId)
        .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
```

Controller에는 try-catch를 작성하지 않는다.

금지:

```java
@GetMapping("/{id}")
public ResponseEntity<?> getPost(@PathVariable Long id) {
    try {
        return ResponseEntity.ok(postService.getPost(id));
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
```

권장:

```java
@GetMapping("/{id}")
public ResponseEntity<ApiResponse<PostResponse>> getPost(@PathVariable Long id) {
    PostResponse response = postService.getPost(id);
    return ResponseEntity.ok(ApiResponse.success(response));
}
```

---

## 9. 요청값 검증 규칙

Request DTO에는 Bean Validation을 사용한다.

예시:

```java
@Getter
@NoArgsConstructor
public class PostCreateRequest {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;
}
```

Controller에서는 반드시 `@Valid`를 붙인다.

```java
@PostMapping
public ResponseEntity<ApiResponse<PostResponse>> createPost(
        @Valid @RequestBody PostCreateRequest request
) {
    PostResponse response = postService.createPost(request);

    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success(response));
}
```

검증 실패는 `GlobalExceptionHandler`가 자동으로 처리한다.

---

## 10. PageResponse 사용 규칙

전체 개수와 전체 페이지 수가 필요한 게시판형 목록은 `PageResponse<T>`를 사용한다.

예시 상황:

```text
게시글 목록
팀 목록
예약 목록
장소 목록
검색 결과 목록
관리자 목록
```

Controller 예시:

```java
@GetMapping
public ResponseEntity<ApiResponse<PageResponse<PostResponse>>> getPosts(
        @PageableDefault(size = 10) Pageable pageable
) {
    PageResponse<PostResponse> response = postService.getPosts(pageable);
    return ResponseEntity.ok(ApiResponse.success(response));
}
```

Service 예시:

```java
@Transactional(readOnly = true)
public PageResponse<PostResponse> getPosts(Pageable pageable) {
    Page<Post> postPage = postRepository.findAll(pageable);
    Page<PostResponse> responsePage = postPage.map(PostResponse::from);

    return PageResponse.from(responsePage);
}
```

응답 예시:

```json
{
  "result": "SUCCESS",
  "data": {
    "content": [
      {
        "id": 1,
        "title": "첫 번째 글"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 32,
    "totalPages": 4,
    "numberOfElements": 10,
    "first": true,
    "last": false,
    "empty": false
  }
}
```

금지:

```java
return ResponseEntity.ok(ApiResponse.success(postPage));
```

권장:

```java
return ResponseEntity.ok(ApiResponse.success(PageResponse.from(responsePage)));
```

---

## 11. SliceResponse 관련 주의

현재 공통 계층에 `SliceResponse`가 아직 없다면, 코딩 에이전트는 임의로 `SliceResponse`를 사용하면 안 된다.

무한 스크롤 API가 필요한 경우에는 먼저 별도 공통 작업으로 `SliceResponse`를 추가한 뒤 사용한다.

현재 기준:

```text
게시판형 목록 → PageResponse 사용
무한 스크롤 → SliceResponse 추가 작업 후 사용
```

---

## 12. BaseEntity 사용 규칙

새로 만드는 대부분의 도메인 Entity는 `BaseEntity` 상속을 우선 고려한다.

예시:

```java
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;
}
```

`BaseEntity`를 상속하면 다음 컬럼이 추가된다.

```text
created_at
updated_at
```

`createdAt`, `updatedAt`은 직접 설정하지 않는다.

금지:

```java
this.createdAt = LocalDateTime.now();
this.updatedAt = LocalDateTime.now();
```

JPA Auditing이 자동으로 처리한다.

---

## 13. 기존 User Entity 수정 금지

현재 `User` Entity는 Auth/회원 도메인과 연결된 핵심 Entity다.

코딩 에이전트는 명시적인 요청이 없는 한 다음 작업을 하면 안 된다.

```java
public class User extends BaseEntity
```

이유:

```text
users 테이블에 created_at, updated_at 컬럼이 추가됨
기존 인증/회원 기능에 영향 가능
다른 팀원 담당 영역 침범 가능
DB 스키마 변경 발생
```

기존 `User`에 `BaseEntity`를 적용하려면 별도 요청이나 팀 합의가 있어야 한다.

---

## 14. Entity 응답 반환 규칙

Entity를 API 응답으로 직접 반환하지 않는다.

금지:

```java
return ResponseEntity.ok(ApiResponse.success(post));
```

권장:

```java
return ResponseEntity.ok(ApiResponse.success(PostResponse.from(post)));
```

Response DTO 예시:

```java
@Getter
@Builder
public class PostResponse {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    public static PostResponse from(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
```

---

## 15. 새 도메인 생성 기본 패턴

새 기능을 구현할 때는 보통 다음 구조를 따른다.

```text
post
 ├── controller
 │    └── PostController.java
 ├── service
 │    └── PostService.java
 ├── repository
 │    └── PostRepository.java
 ├── dto
 │    ├── PostCreateRequest.java
 │    ├── PostUpdateRequest.java
 │    └── PostResponse.java
 └── entity
      └── Post.java
```

도메인 생성 순서:

```text
1. Entity 생성
2. Request DTO 생성
3. Response DTO 생성
4. Repository 생성
5. ErrorCode 추가
6. Service 생성
7. Controller 생성
8. 필요하면 PageResponse 적용
```

---

## 16. 단일 조회 API 템플릿

Controller:

```java
@GetMapping("/{id}")
public ResponseEntity<ApiResponse<PostResponse>> getPost(@PathVariable Long id) {
    PostResponse response = postService.getPost(id);
    return ResponseEntity.ok(ApiResponse.success(response));
}
```

Service:

```java
@Transactional(readOnly = true)
public PostResponse getPost(Long postId) {
    Post post = postRepository.findById(postId)
            .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

    return PostResponse.from(post);
}
```

---

## 17. 생성 API 템플릿

Controller:

```java
@PostMapping
public ResponseEntity<ApiResponse<PostResponse>> createPost(
        @Valid @RequestBody PostCreateRequest request
) {
    PostResponse response = postService.createPost(request);

    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success(response));
}
```

Service:

```java
@Transactional
public PostResponse createPost(PostCreateRequest request) {
    Post post = Post.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .build();

    Post savedPost = postRepository.save(post);

    return PostResponse.from(savedPost);
}
```

---

## 18. 수정 API 템플릿

Controller:

```java
@PatchMapping("/{id}")
public ResponseEntity<ApiResponse<PostResponse>> updatePost(
        @PathVariable Long id,
        @Valid @RequestBody PostUpdateRequest request
) {
    PostResponse response = postService.updatePost(id, request);
    return ResponseEntity.ok(ApiResponse.success(response));
}
```

Service:

```java
@Transactional
public PostResponse updatePost(Long postId, PostUpdateRequest request) {
    Post post = postRepository.findById(postId)
            .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

    post.update(request.getTitle(), request.getContent());

    return PostResponse.from(post);
}
```

---

## 19. 삭제 API 템플릿

Controller:

```java
@DeleteMapping("/{id}")
public ResponseEntity<Void> deletePost(@PathVariable Long id) {
    postService.deletePost(id);
    return ResponseEntity.noContent().build();
}
```

Service:

```java
@Transactional
public void deletePost(Long postId) {
    Post post = postRepository.findById(postId)
            .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

    postRepository.delete(post);
}
```

---

## 20. 목록 API 템플릿

Controller:

```java
@GetMapping
public ResponseEntity<ApiResponse<PageResponse<PostResponse>>> getPosts(
        @PageableDefault(size = 10) Pageable pageable
) {
    PageResponse<PostResponse> response = postService.getPosts(pageable);
    return ResponseEntity.ok(ApiResponse.success(response));
}
```

Service:

```java
@Transactional(readOnly = true)
public PageResponse<PostResponse> getPosts(Pageable pageable) {
    Page<Post> postPage = postRepository.findAll(pageable);
    Page<PostResponse> responsePage = postPage.map(PostResponse::from);

    return PageResponse.from(responsePage);
}
```

---

## 21. 코딩 에이전트 작업 시 금지 목록

코딩 에이전트는 다음 작업을 하면 안 된다.

```text
com.example.* 패키지 생성 금지
Controller에서 try-catch로 에러 응답 생성 금지
Service에서 ResponseEntity 반환 금지
RuntimeException 직접 throw 금지
문자열 에러 코드 직접 작성 금지
Entity 직접 응답 반환 금지
Page<T>를 그대로 응답 반환 금지
기존 Auth/Security 코드 임의 수정 금지
기존 User Entity 임의 수정 금지
BaseEntity에 deletedAt/isDeleted 임의 추가 금지
SliceResponse가 없는데 임의 사용 금지
```

---

## 22. 코딩 에이전트 작업 시 권장 목록

코딩 에이전트는 다음 방식을 우선 사용한다.

```text
성공 응답은 ApiResponse.success(...)
비즈니스 실패는 BusinessException(ErrorCode.XXX)
에러 코드는 ErrorCode에 먼저 추가
목록 응답은 PageResponse.from(...)
Entity는 BaseEntity 상속 우선 고려
Entity는 Response DTO로 변환
Request DTO에는 @Valid 관련 어노테이션 추가
Controller에는 @Valid 적용
Service에는 @Transactional 적용
조회 전용 Service에는 @Transactional(readOnly = true) 적용
```

---

## 23. 작업 요청을 받았을 때 확인할 것

코딩 에이전트는 작업 전 다음을 확인한다.

```text
1. 대상 도메인이 기존에 존재하는가?
2. 기존 담당자가 만든 Auth/Security/User 영역을 건드리는가?
3. 새 ErrorCode가 필요한가?
4. 응답 DTO가 필요한가?
5. 목록 조회인가?
6. PageResponse가 필요한가?
7. Entity가 BaseEntity를 상속해야 하는가?
8. @Valid 검증이 필요한 Request DTO인가?
9. 기존 공통 응답 포맷을 깨지 않는가?
```

---

## 24. 최종 요약

이 프로젝트에서 API를 만들 때 가장 중요한 규칙은 다음이다.

```text
성공하면 ApiResponse.success(...)
실패하면 BusinessException(ErrorCode.XXX)
에러 응답은 GlobalExceptionHandler가 만든다.
목록이면 PageResponse를 사용한다.
Entity는 BaseEntity를 상속하고 DTO로 변환한다.
기존 Auth/Security/User 코드는 명시적 요청 없이 수정하지 않는다.
```

코딩 에이전트는 새 코드를 생성할 때 이 규칙을 우선 적용해야 한다.
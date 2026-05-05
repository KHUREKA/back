# 페이징 응답 컨벤션

## 1. 목적

이 문서는 KHUREKA 백엔드에서 목록 조회 API를 구현할 때 사용하는 페이징 응답 형식을 정리한다.

목록 조회는 대부분의 도메인에서 반복된다.

```text
게시글 목록
팀 목록
모집글 목록
장소 목록
예약 목록
채팅방 목록
알림 목록
```

따라서 각 API마다 페이징 응답 구조를 다르게 만들지 않고, 공통 `PageResponse<T>`를 사용한다.

---

## 2. 기본 응답 구조

페이징 응답은 `ApiResponse`의 `data` 안에 `PageResponse<T>`를 담는다.

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

---

## 3. PageResponse 필드 설명

| 필드 | 설명 |
|---|---|
| `content` | 현재 페이지의 실제 데이터 목록 |
| `page` | 현재 페이지 번호 |
| `size` | 요청한 페이지 크기 |
| `totalElements` | 전체 데이터 개수 |
| `totalPages` | 전체 페이지 수 |
| `numberOfElements` | 현재 페이지에 실제로 담긴 데이터 개수 |
| `first` | 첫 번째 페이지 여부 |
| `last` | 마지막 페이지 여부 |
| `empty` | 현재 페이지가 비어 있는지 여부 |

---

## 4. 페이지 번호 규칙

Spring Data JPA의 `Pageable`은 기본적으로 페이지 번호가 `0`부터 시작한다.

```text
첫 번째 페이지 = page 0
두 번째 페이지 = page 1
세 번째 페이지 = page 2
```

예시 요청:

```http
GET /api/v1/posts?page=0&size=10
```

이 요청은 첫 번째 페이지에서 10개를 조회한다는 의미다.

---

## 5. Controller 사용 예시

Controller는 `ApiResponse.success(PageResponse)` 형태로 응답한다.

```java
@GetMapping
public ResponseEntity<ApiResponse<PageResponse<PostResponse>>> getPosts(
        @PageableDefault(size = 10) Pageable pageable
) {
    PageResponse<PostResponse> response = postService.getPosts(pageable);
    return ResponseEntity.ok(ApiResponse.success(response));
}
```

---

## 6. Service 사용 예시 1 — Page.map 사용

가장 추천하는 방식이다.

Repository에서 `Page<Entity>`를 조회한 뒤, `page.map(...)`으로 DTO Page로 변환한다.

```java
@Transactional(readOnly = true)
public PageResponse<PostResponse> getPosts(Pageable pageable) {
    Page<Post> postPage = postRepository.findAll(pageable);

    Page<PostResponse> responsePage = postPage.map(PostResponse::from);

    return PageResponse.from(responsePage);
}
```

장점:

```text
Spring Data Page의 메타데이터를 그대로 유지할 수 있다.
코드가 짧고 명확하다.
PageResponse.from(Page<T>)를 바로 사용할 수 있다.
```

---

## 7. Service 사용 예시 2 — content를 직접 변환

복잡한 DTO 변환이 필요할 때는 `content`를 직접 만든 뒤, 원본 `Page`의 메타데이터와 합친다.

```java
@Transactional(readOnly = true)
public PageResponse<PostResponse> getPosts(Pageable pageable) {
    Page<Post> postPage = postRepository.findAll(pageable);

    List<PostResponse> content = postPage.getContent()
            .stream()
            .map(post -> PostResponse.of(
                    post.getId(),
                    post.getTitle(),
                    post.getWriter().getUsername()
            ))
            .toList();

    return PageResponse.from(postPage, content);
}
```

이 방식은 다음 상황에서 사용한다.

```text
DTO 변환 과정에서 여러 필드를 조합해야 할 때
Entity 단순 변환이 아니라 추가 계산이 필요할 때
Page.map(...)만으로 표현하기 애매할 때
```

---

## 8. 정렬 요청 규칙

정렬이 필요한 목록 API는 Spring Data의 `sort` 파라미터를 사용할 수 있다.

예시:

```http
GET /api/v1/posts?page=0&size=10&sort=createdAt,desc
```

의미:

```text
첫 번째 페이지를 10개 조회
createdAt 기준 내림차순 정렬
```

여러 정렬 조건이 필요하면 다음처럼 요청할 수 있다.

```http
GET /api/v1/posts?page=0&size=10&sort=createdAt,desc&sort=id,desc
```

---

## 9. 기본 페이지 크기

별도 요구사항이 없다면 기본 페이지 크기는 `10`으로 둔다.

```java
@GetMapping
public ResponseEntity<ApiResponse<PageResponse<PostResponse>>> getPosts(
        @PageableDefault(size = 10) Pageable pageable
) {
    PageResponse<PostResponse> response = postService.getPosts(pageable);
    return ResponseEntity.ok(ApiResponse.success(response));
}
```

도메인 특성에 따라 기본 페이지 크기를 다르게 둘 수 있다.

예시:

```text
일반 목록: 10
채팅 메시지: 30 또는 50
알림 목록: 20
검색 결과: 10
```

---

## 10. 사용 금지 규칙

### 10.1 Page<Entity> 직접 반환 금지

Entity가 API 응답으로 직접 노출되면 안 된다.

```java
// 금지
@GetMapping
public ResponseEntity<ApiResponse<Page<Post>>> getPosts(Pageable pageable) {
    return ResponseEntity.ok(ApiResponse.success(postRepository.findAll(pageable)));
}
```

반드시 응답 DTO로 변환한다.

```java
// 권장
@GetMapping
public ResponseEntity<ApiResponse<PageResponse<PostResponse>>> getPosts(Pageable pageable) {
    PageResponse<PostResponse> response = postService.getPosts(pageable);
    return ResponseEntity.ok(ApiResponse.success(response));
}
```

---

### 10.2 Spring Data Page를 그대로 반환 금지

`Page<T>`를 그대로 반환하면 응답 구조가 복잡하고, 프론트엔드가 필요 없는 내부 메타데이터까지 받게 된다.

```java
// 금지
return ResponseEntity.ok(ApiResponse.success(responsePage));
```

`PageResponse<T>`로 감싼다.

```java
// 권장
return ResponseEntity.ok(ApiResponse.success(PageResponse.from(responsePage)));
```

---

### 10.3 도메인마다 페이징 응답 DTO를 따로 만들지 않기

다음처럼 도메인별로 유사한 페이징 DTO를 반복해서 만들지 않는다.

```text
PostPageResponse
TeamPageResponse
PlacePageResponse
ReservationPageResponse
```

공통 `PageResponse<T>`를 사용한다.

```java
PageResponse<PostResponse>
PageResponse<TeamResponse>
PageResponse<PlaceResponse>
PageResponse<ReservationResponse>
```

---

## 11. 최종 사용 패턴

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

응답:

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
# KHUREKA 공통 계층 작업 정리

## 1. 개요

현재까지 작업한 공통 계층은 크게 3가지 묶음으로 나눌 수 있다.

```text
1. 공통 API 응답/에러 처리 계층
2. 공통 페이징 응답 계층
3. 공통 Entity 시간 관리 계층
```

이 작업들은 해커톤 주제가 정해지기 전에도 거의 모든 도메인에서 재사용할 수 있는 기반 코드다.

---

## 2. 공통 API 응답/에러 처리 계층

### 2.1 작업 파일

```text
src/main/java/com/khureka/server/common/response/ApiResponse.java
src/main/java/com/khureka/server/common/response/ErrorResponse.java
src/main/java/com/khureka/server/common/exception/ErrorCode.java
src/main/java/com/khureka/server/common/exception/BusinessException.java
src/main/java/com/khureka/server/common/exception/GlobalExceptionHandler.java
```

### 2.2 관련 문서

```text
docs/API_RESPONSE_CONVENTION.md
docs/EXCEPTION_HANDLING_CONVENTION.md
```

---

## 3. ApiResponse

`ApiResponse`는 API의 최상위 응답 형식을 통일하기 위한 클래스다.

성공 응답이든 에러 응답이든 최상위에 `result`를 두고, 성공이면 `data`, 실패면 `error`를 담는 구조다.

---

### 3.1 성공 응답 예시

```json
{
  "result": "SUCCESS",
  "data": {
    "id": 1,
    "username": "sunz"
  }
}
```

---

### 3.2 데이터 없는 성공 응답 예시

```json
{
  "result": "SUCCESS"
}
```

---

### 3.3 에러 응답 예시

```json
{
  "result": "ERROR",
  "error": {
    "code": "USER-409-001",
    "message": "이미 사용 중인 이메일입니다.",
    "timestamp": "2026-05-05T18:10:22.123+09:00",
    "path": "/api/v1/auth/signup"
  }
}
```

---

### 3.4 ApiResponse의 역할

```text
성공/실패 응답의 최상위 구조를 통일한다.
성공 시 data를 담는다.
실패 시 error를 담는다.
프론트엔드가 result 값으로 성공/실패를 쉽게 구분할 수 있게 한다.
```

---

### 3.5 사용 예시

데이터가 있는 성공 응답:

```java
return ResponseEntity.ok(ApiResponse.success(response));
```

데이터가 없는 성공 응답:

```java
return ResponseEntity.ok(ApiResponse.success());
```

에러 응답은 보통 Controller나 Service에서 직접 만들지 않고, `GlobalExceptionHandler`에서 만든다.

---

## 4. ErrorResponse

`ErrorResponse`는 에러가 발생했을 때 `ApiResponse`의 `error` 필드에 들어가는 상세 정보 객체다.

---

### 4.1 포함 정보

```text
code      → 프론트엔드 분기 처리를 위한 에러 코드
message   → 사용자에게 보여줄 수 있는 한국어 메시지
timestamp → 에러 발생 시각
path      → 에러가 발생한 요청 URI
errors    → @Valid 검증 실패 시 필드별 에러 목록
```

---

### 4.2 기본 에러 응답 예시

```json
{
  "code": "USER-404-001",
  "message": "사용자를 찾을 수 없습니다.",
  "timestamp": "2026-05-05T18:10:22.123+09:00",
  "path": "/api/v1/users/1"
}
```

---

### 4.3 검증 실패 응답 예시

```json
{
  "code": "COMMON-400",
  "message": "이메일 형식이 올바르지 않습니다.",
  "timestamp": "2026-05-05T18:10:22.123+09:00",
  "path": "/api/v1/auth/signup",
  "errors": [
    {
      "field": "email",
      "reason": "이메일 형식이 올바르지 않습니다."
    }
  ]
}
```

---

### 4.4 ErrorResponse의 역할

`ErrorResponse`의 핵심은 에러 응답 구조를 API마다 다르게 만들지 않도록 막는 것이다.

즉, 어떤 API에서 에러가 발생하더라도 프론트엔드는 항상 같은 구조의 에러 응답을 받을 수 있다.

---

## 5. ErrorCode

`ErrorCode`는 프로젝트 전체에서 사용할 에러 코드를 한 곳에 모아둔 enum이다.

각 에러는 다음 정보를 가진다.

```text
code       → "USER-409-001" 같은 식별 코드
httpStatus → 400, 401, 403, 404, 409, 500 등 HTTP 상태 코드
message    → 기본 에러 메시지
```

---

### 5.1 ErrorCode 예시

```java
USER_DUPLICATE_EMAIL("USER-409-001", 409, "이미 사용 중인 이메일입니다.")
```

---

### 5.2 ErrorCode를 사용하는 이유

서비스 코드 곳곳에서 문자열 메시지를 직접 쓰지 않기 위해서다.

#### 나쁜 예

```java
throw new RuntimeException("이미 사용 중인 이메일입니다.");
```

#### 좋은 예

```java
throw new BusinessException(ErrorCode.USER_DUPLICATE_EMAIL);
```

---

### 5.3 ErrorCode의 장점

```text
에러 코드가 한 곳에서 관리된다.
프론트엔드와 에러 코드 기준으로 소통할 수 있다.
HTTP 상태 코드와 메시지가 흩어지지 않는다.
새 도메인이 생겨도 에러 코드 추가 규칙을 유지할 수 있다.
```

---

### 5.4 현재 에러 코드 형식

현재 에러 코드 형식은 다음 방향으로 잡았다.

```text
COMMON-400
AUTH-401-001
USER-404-001
POST-404-001
```

도메인별로 확장할 때는 다음처럼 추가하면 된다.

```java
TEAM_NOT_FOUND("TEAM-404-001", 404, "팀을 찾을 수 없습니다."),
TEAM_ALREADY_JOINED("TEAM-409-001", 409, "이미 참여한 팀입니다.")
```

---

## 6. BusinessException

`BusinessException`은 서비스 로직에서 발생하는 비즈니스 예외의 공통 부모 클래스다.

기존에는 다음과 같이 예외를 던질 수 있었다.

```java
throw new RuntimeException("Email already exists");
```

하지만 이렇게 하면 이 예외가 `400`인지, `409`인지, 어떤 에러 코드인지 알기 어렵다.

따라서 이제는 다음처럼 사용한다.

```java
throw new BusinessException(ErrorCode.USER_DUPLICATE_EMAIL);
```

---

### 6.1 상세 메시지가 필요한 경우

```java
throw new BusinessException(
        ErrorCode.USER_NOT_FOUND,
        "ID " + userId + "에 해당하는 사용자를 찾을 수 없습니다."
);
```

---

### 6.2 BusinessException의 역할

```text
비즈니스 실패 상황을 표현한다.
ErrorCode를 함께 들고 있다.
GlobalExceptionHandler가 ErrorCode를 보고 HTTP 상태 코드와 응답 메시지를 만들 수 있게 한다.
RuntimeException을 직접 던지는 것을 줄인다.
```

---

## 7. GlobalExceptionHandler

`GlobalExceptionHandler`는 프로젝트 전역에서 발생하는 예외를 한 곳에서 처리하는 클래스다.

Controller마다 `try-catch`를 쓰지 않고, 예외가 발생하면 여기서 공통 응답으로 바꿔준다.

---

### 7.1 예외 처리 흐름

```text
Service에서 BusinessException 발생
→ Controller 밖으로 예외 전파
→ GlobalExceptionHandler가 예외 처리
→ ErrorCode 기반 ErrorResponse 생성
→ ApiResponse.error(...)로 최종 응답 반환
```

---

### 7.2 사용 예시

Service에서 다음과 같이 예외를 던진다.

```java
throw new BusinessException(ErrorCode.USER_DUPLICATE_EMAIL);
```

그러면 최종 응답은 다음처럼 나간다.

```json
{
  "result": "ERROR",
  "error": {
    "code": "USER-409-001",
    "message": "이미 사용 중인 이메일입니다.",
    "timestamp": "2026-05-05T18:10:22.123+09:00",
    "path": "/api/v1/auth/signup"
  }
}
```

---

### 7.3 현재 GlobalExceptionHandler에서 처리하는 예외

```text
BusinessException
MethodArgumentNotValidException
MethodArgumentTypeMismatchException
HttpRequestMethodNotSupportedException
HttpMessageNotReadableException
AuthenticationException
AccessDeniedException
Exception
```

---

### 7.4 예외별 의미

```text
BusinessException
→ 우리가 직접 정의한 비즈니스 예외

MethodArgumentNotValidException
→ @Valid 검증 실패

MethodArgumentTypeMismatchException
→ PathVariable, RequestParam 타입 불일치

HttpRequestMethodNotSupportedException
→ 지원하지 않는 HTTP 메서드 요청

HttpMessageNotReadableException
→ JSON 파싱 실패, 요청 Body 문제

AuthenticationException
→ 로그인 인증 실패

AccessDeniedException
→ 권한 없음

Exception
→ 예상하지 못한 서버 오류
```

---

### 7.5 Exception 처리 시 주의사항

특히 마지막 `Exception` 처리는 중요하다.

```text
서버 로그에는 스택 트레이스를 남긴다.
클라이언트에게는 내부 에러 상세를 노출하지 않는다.
```

---

## 8. 공통 페이징 응답 계층

### 8.1 작업 파일

```text
src/main/java/com/khureka/server/common/response/PageResponse.java
```

### 8.2 관련 문서

```text
docs/PAGING_RESPONSE_CONVENTION.md
```

---

## 9. PageResponse

`PageResponse`는 목록 조회 API의 응답 형식을 통일하기 위한 공통 DTO다.

게시글 목록, 팀 목록, 장소 목록, 예약 목록, 채팅방 목록처럼 대부분의 도메인에서 목록 조회가 생기기 때문에 미리 만들어둔 것이다.

최종 응답은 `ApiResponse`의 `data` 안에 `PageResponse`가 들어가는 구조다.

---

### 9.1 PageResponse 응답 예시

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

### 9.2 PageResponse 필드 설명

```text
content          → 현재 페이지 데이터 목록
page             → 현재 페이지 번호
size             → 요청한 페이지 크기
totalElements    → 전체 데이터 개수
totalPages       → 전체 페이지 수
numberOfElements → 현재 페이지에 실제 담긴 데이터 개수
first            → 첫 페이지 여부
last             → 마지막 페이지 여부
empty            → 현재 페이지가 비어 있는지 여부
```

---

### 9.3 사용 예시

```java
Page<Post> postPage = postRepository.findAll(pageable);
Page<PostResponse> responsePage = postPage.map(PostResponse::from);

return PageResponse.from(responsePage);
```

Controller에서는 다음처럼 반환한다.

```java
return ResponseEntity.ok(ApiResponse.success(response));
```

즉, 최종 타입은 보통 다음과 같다.

```java
ResponseEntity<ApiResponse<PageResponse<PostResponse>>>
```

---

### 9.4 PageResponse를 만든 이유

```text
도메인마다 PostPageResponse, TeamPageResponse 같은 중복 DTO를 만들지 않기 위해서
Spring Data Page를 그대로 반환하지 않기 위해서
프론트엔드가 항상 같은 페이징 구조를 기대할 수 있게 하기 위해서
Entity가 API 응답으로 직접 노출되는 것을 막기 위해서
```

---

## 10. 공통 Entity 시간 관리 계층

### 10.1 작업 파일

```text
src/main/java/com/khureka/server/common/entity/BaseEntity.java
src/main/java/com/khureka/server/config/JpaAuditingConfig.java
```

### 10.2 관련 문서

```text
docs/ENTITY_CONVENTION.md
```

---

## 11. BaseEntity

`BaseEntity`는 모든 JPA Entity에서 공통으로 사용할 생성 시간, 수정 시간을 관리하는 추상 클래스다.

필드는 두 개다.

```java
private LocalDateTime createdAt;
private LocalDateTime updatedAt;
```

---

### 11.1 createdAt / updatedAt 의미

```text
createdAt
→ Entity가 처음 저장된 시각

updatedAt
→ Entity가 마지막으로 수정된 시각
```

---

### 11.2 사용 예시

새로운 도메인 Entity는 다음처럼 상속해서 사용한다.

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

그러면 `Post` 테이블에는 자동으로 다음 컬럼이 들어간다.

```text
created_at
updated_at
```

---

### 11.3 @MappedSuperclass

중요한 점은 `BaseEntity` 자체는 테이블로 만들어지지 않는다는 것이다.

```java
@MappedSuperclass
```

이 어노테이션 때문에 `BaseEntity`는 별도 테이블이 되지 않고, 상속받는 Entity의 컬럼으로만 포함된다.

---

## 12. JpaAuditingConfig

`BaseEntity`의 `createdAt`, `updatedAt`이 자동으로 동작하려면 JPA Auditing이 켜져 있어야 한다.

그래서 추가한 설정이 다음 클래스다.

```java
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
```

이 설정이 있어야 아래 어노테이션들이 동작한다.

```java
@CreatedDate
@LastModifiedDate
```

즉, 개발자가 직접 다음처럼 시간을 넣지 않아도 된다.

```java
this.createdAt = LocalDateTime.now();
this.updatedAt = LocalDateTime.now();
```

JPA가 저장/수정 시점에 자동으로 넣어준다.

---

## 13. 기존 User에는 아직 적용하지 않은 이유

현재 `User` 엔티티는 다른 백엔드 팀원이 작업한 인증/회원 핵심 엔티티다.

그래서 바로 다음처럼 수정하지 않았다.

```java
public class User extends BaseEntity
```

이유는 다음과 같다.

```text
User 테이블에 created_at, updated_at 컬럼이 추가됨
기존 인증/회원 기능에 영향이 있을 수 있음
담당자 영역을 침범할 수 있음
DB 스키마가 바뀌기 때문에 팀 합의가 필요함
```

따라서 이번 작업은 안전하게 여기까지만 진행했다.

```text
BaseEntity 생성
JpaAuditingConfig 생성
문서 작성
기존 User는 수정하지 않음
```

나중에 팀 합의가 되면 별도 PR에서 `User extends BaseEntity`를 적용하면 된다.

---

## 14. 지금까지 작업한 코드들의 연결 관계

전체적으로 보면 연결 구조는 다음과 같다.

```text
ApiResponse
 ├── 성공 응답 최상위 래퍼
 └── 에러 응답 최상위 래퍼

ErrorResponse
 └── ApiResponse의 error 필드에 들어가는 에러 상세 객체

ErrorCode
 └── ErrorResponse 생성에 필요한 code, status, message 관리

BusinessException
 └── Service에서 ErrorCode를 들고 던지는 비즈니스 예외

GlobalExceptionHandler
 ├── BusinessException을 잡음
 ├── ErrorCode를 꺼냄
 ├── ErrorResponse를 만듦
 └── ApiResponse.error(...)로 반환

PageResponse
 └── 목록 API에서 ApiResponse의 data 안에 들어가는 페이징 응답 DTO

BaseEntity
 └── 새 Entity들이 상속해서 createdAt, updatedAt을 공통 사용

JpaAuditingConfig
 └── BaseEntity의 createdAt, updatedAt 자동 기록을 활성화
```

---

## 15. 실제 개발 시 사용 흐름

예를 들어 나중에 게시글 목록 API를 만든다고 하면 흐름은 다음과 같다.

---

### 15.1 Entity

```java
@Entity
public class Post extends BaseEntity {
    ...
}
```

---

### 15.2 Service

```java
@Transactional(readOnly = true)
public PageResponse<PostResponse> getPosts(Pageable pageable) {
    Page<Post> postPage = postRepository.findAll(pageable);
    Page<PostResponse> responsePage = postPage.map(PostResponse::from);

    return PageResponse.from(responsePage);
}
```

---

### 15.3 Controller

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

### 15.4 성공 응답

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

### 15.5 에러 응답 흐름

만약 게시글을 찾지 못하면 Service에서 다음처럼 예외를 던진다.

```java
throw new BusinessException(ErrorCode.POST_NOT_FOUND);
```

그러면 `GlobalExceptionHandler`가 처리해서 다음처럼 응답한다.

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

---

## 16. 지금까지 작업의 의미

이번 작업들은 단순히 파일 몇 개를 추가한 것이 아니라, 앞으로 팀이 도메인 기능을 만들 때 따라야 할 기반 규칙을 만든 것이다.

정리하면 다음과 같다.

```text
ApiResponse
→ 성공/실패 응답의 최상위 구조 통일

ErrorResponse
→ 에러 응답 상세 구조 통일

ErrorCode
→ 에러 코드와 HTTP 상태 코드 중앙 관리

BusinessException
→ 서비스 계층의 비즈니스 예외 표준화

GlobalExceptionHandler
→ 예외 응답 변환을 한 곳으로 집중

PageResponse
→ 목록 조회 응답 형식 통일

BaseEntity
→ Entity 생성/수정 시간 관리 표준화

JpaAuditingConfig
→ createdAt, updatedAt 자동 기록 활성화
```
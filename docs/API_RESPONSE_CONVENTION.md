# API 응답 공통 계층 컨벤션

## 1. 목적

이 문서는 KHUREKA 백엔드의 API 응답 형식과 공통 응답 객체 사용 규칙을 정리한다.

현재 API 응답 공통 계층은 다음 객체를 중심으로 구성한다.

```text
ApiResponse   → API 최상위 응답 래퍼
ErrorResponse → 에러 상세 응답 객체
ErrorCode     → 에러 코드, HTTP 상태 코드, 기본 메시지 중앙 관리
```

이 컨벤션의 목표는 다음과 같다.

```text
성공 응답 형식 통일
에러 응답 형식 통일
프론트엔드 에러 분기 기준 통일
도메인별 에러 코드 관리 방식 통일
```

---

## 2. 최상위 응답 구조

모든 API 응답은 기본적으로 `ApiResponse`를 기준으로 한다.

### 성공 응답

```json
{
  "result": "SUCCESS",
  "data": {
    "id": 1,
    "username": "sunz"
  }
}
```

### 데이터 없는 성공 응답

```json
{
  "result": "SUCCESS"
}
```

### 에러 응답

```json
{
  "result": "ERROR",
  "error": {
    "code": "USER-404-001",
    "message": "사용자를 찾을 수 없습니다.",
    "timestamp": "2026-05-05T18:10:22.123+09:00",
    "path": "/api/v1/users/1"
  }
}
```

---

## 3. ApiResponse 사용 규칙

`ApiResponse`는 API의 최상위 응답 래퍼다.

### 3.1 데이터가 있는 성공 응답

데이터가 있는 성공 응답은 `ApiResponse.success(data)`를 사용한다.

```java
@GetMapping("/me")
public ResponseEntity<ApiResponse<UserResponse>> getMe() {
    UserResponse response = userService.getMe();
    return ResponseEntity.ok(ApiResponse.success(response));
}
```

응답 예시:

```json
{
  "result": "SUCCESS",
  "data": {
    "id": 1,
    "email": "test@example.com",
    "username": "sunz"
  }
}
```

---

### 3.2 데이터가 없는 성공 응답

데이터가 없는 성공 응답은 `ApiResponse.success()`를 사용한다.

```java
@PostMapping("/logout")
public ResponseEntity<ApiResponse<Void>> logout() {
    authService.logout();
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

### 3.3 생성 성공 응답

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

### 3.4 삭제 성공 응답

삭제 성공은 `204 No Content`를 사용한다.

삭제 응답에는 body를 내려주지 않는다.

```java
@DeleteMapping("/{id}")
public ResponseEntity<Void> deletePost(@PathVariable Long id) {
    postService.deletePost(id);
    return ResponseEntity.noContent().build();
}
```

응답:

```http
204 No Content
```

---

## 4. ErrorResponse 사용 규칙

`ErrorResponse`는 에러 상세 정보를 담는 객체다.

일반적으로 API 최상위 응답으로 직접 반환하지 않고, `ApiResponse`의 `error` 필드에 들어간다.

```json
{
  "result": "ERROR",
  "error": {
    "code": "AUTH-401-001",
    "message": "이메일 또는 비밀번호가 올바르지 않습니다.",
    "timestamp": "2026-05-05T18:10:22.123+09:00",
    "path": "/api/v1/auth/login"
  }
}
```

### 필드 설명

| 필드 | 설명 |
|---|---|
| `code` | 프론트엔드 분기 처리를 위한 에러 코드 |
| `message` | 사용자에게 보여줄 수 있는 한국어 메시지 |
| `timestamp` | 에러 발생 시각 |
| `path` | 에러가 발생한 요청 URI |
| `errors` | `@Valid` 검증 실패 시 필드별 에러 목록 |

---

## 5. 검증 실패 응답

`@Valid` 검증 실패 시에는 `errors` 필드를 포함한다.

```json
{
  "result": "ERROR",
  "error": {
    "code": "COMMON-400",
    "message": "이메일 형식이 올바르지 않습니다.",
    "timestamp": "2026-05-05T18:10:22.123+09:00",
    "path": "/api/v1/auth/signup",
    "errors": [
      {
        "field": "email",
        "reason": "이메일 형식이 올바르지 않습니다."
      },
      {
        "field": "password",
        "reason": "비밀번호는 8자 이상이어야 합니다."
      }
    ]
  }
}
```

요청 DTO에는 Bean Validation을 붙인다.

```java
@Getter
@NoArgsConstructor
public class SignupRequest {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "사용자 이름은 필수입니다.")
    private String username;
}
```

Controller에서는 `@Valid`를 붙인다.

```java
@PostMapping("/signup")
public ResponseEntity<ApiResponse<Void>> signup(
        @Valid @RequestBody SignupRequest request
) {
    authService.signup(request);
    return ResponseEntity.ok(ApiResponse.success());
}
```

---

## 6. ErrorCode 작성 규칙

모든 에러 코드는 `ErrorCode` enum에서 관리한다.

### 코드 형식

```text
COMMON-400
AUTH-401-001
USER-404-001
POST-404-001
```

### 작성 규칙

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
USER_DUPLICATE_EMAIL("USER-409-001", 409, "이미 사용 중인 이메일입니다."),
POST_NOT_FOUND("POST-404-001", 404, "게시글을 찾을 수 없습니다.");
```

---

## 7. ErrorCode 추가 기준

새로운 에러 상황이 생기면 먼저 `ErrorCode`에 항목을 추가한다.

예를 들어 팀 도메인이 생기면 다음처럼 추가한다.

```java
TEAM_NOT_FOUND("TEAM-404-001", 404, "팀을 찾을 수 없습니다."),
TEAM_ALREADY_JOINED("TEAM-409-001", 409, "이미 참여한 팀입니다."),
TEAM_JOIN_FORBIDDEN("TEAM-403-001", 403, "해당 팀에 참여할 권한이 없습니다.");
```

서비스에서는 문자열 메시지를 직접 만들기보다 `ErrorCode`를 기준으로 예외를 던진다.

```java
throw new BusinessException(ErrorCode.TEAM_NOT_FOUND);
```

상세 메시지가 필요하면 예외 생성자에서 조립한다.

```java
throw new BusinessException(
        ErrorCode.TEAM_NOT_FOUND,
        "ID " + teamId + "에 해당하는 팀을 찾을 수 없습니다."
);
```

---

## 8. HTTP 상태 코드 기준

| HTTP Status | 사용 상황 | 예시 ErrorCode |
|---|---|---|
| `400 Bad Request` | 요청 값이 잘못됨 | `INVALID_REQUEST` |
| `401 Unauthorized` | 인증 실패 | `UNAUTHORIZED`, `AUTH_INVALID_CREDENTIAL` |
| `403 Forbidden` | 권한 없음 | `FORBIDDEN` |
| `404 Not Found` | 리소스 없음 | `USER_NOT_FOUND`, `POST_NOT_FOUND` |
| `405 Method Not Allowed` | 지원하지 않는 HTTP 메서드 | `METHOD_NOT_ALLOWED` |
| `409 Conflict` | 중복 또는 상태 충돌 | `USER_DUPLICATE_EMAIL` |
| `500 Internal Server Error` | 예상하지 못한 서버 오류 | `INTERNAL_ERROR` |

---

## 9. 사용 금지 규칙

### 성공 응답에 임의 JSON 반환 금지

```java
// 금지
return ResponseEntity.ok(Map.of("message", "성공"));
```

```java
// 권장
return ResponseEntity.ok(ApiResponse.success());
```

---

### 문자열 에러 코드 직접 작성 금지

```java
// 금지
throw new RuntimeException("USER-404-001");
```

```java
// 권장
throw new BusinessException(ErrorCode.USER_NOT_FOUND);
```

---

## 10. 현재 적용 파일

```text
common
 ├── response
 │    ├── ApiResponse.java
 │    └── ErrorResponse.java
 └── exception
      └── ErrorCode.java
```
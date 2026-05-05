# 예외 처리 공통 계층 컨벤션

## 1. 목적

이 문서는 KHUREKA 백엔드에서 사용하는 예외 처리 공통 계층의 역할과 사용 규칙을 정리한다.

현재 예외 처리 계층은 다음 파일을 중심으로 동작한다.

```text
BusinessException      → 비즈니스 예외의 공통 부모
GlobalExceptionHandler → 전역 예외 처리기
ErrorCode              → 에러 코드, HTTP 상태 코드, 기본 메시지 관리
ErrorResponse          → 에러 상세 응답 객체
ApiResponse            → API 최상위 응답 래퍼
```

최종 에러 응답은 다음 형식으로 내려간다.

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

## 2. BusinessException 사용 규칙

`BusinessException`은 서비스 로직에서 발생하는 비즈니스 실패 상황을 표현하는 공통 예외다.

예를 들어 다음 상황에서는 `BusinessException` 또는 이를 상속한 구체 예외를 사용한다.

```text
리소스를 찾을 수 없음
이메일 중복
권한 없음
이미 처리된 요청
비즈니스 규칙 위반
```

### 기본 사용

```java
throw new BusinessException(ErrorCode.USER_DUPLICATE_EMAIL);
```

### 상세 메시지 사용

```java
throw new BusinessException(
        ErrorCode.USER_NOT_FOUND,
        "ID " + userId + "에 해당하는 사용자를 찾을 수 없습니다."
);
```

### 원인 예외 포함

외부 API 호출 실패, 파싱 실패 등 원본 예외를 보존해야 하는 경우 cause를 함께 전달한다.

```java
try {
    // 외부 API 호출
} catch (Exception e) {
    throw new BusinessException(ErrorCode.INTERNAL_ERROR, e);
}
```

---

## 3. Service 계층 예외 처리 규칙

Service 계층에서는 `ResponseEntity`, `HttpStatus`, `ErrorResponse`, `ApiResponse.error(...)`를 직접 사용하지 않는다.

Service는 비즈니스 로직만 처리하고, 실패 상황에서는 예외를 던진다.

### 금지

```java
public ResponseEntity<?> signup(SignupRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
        return ResponseEntity.status(409).body("이미 존재하는 이메일입니다.");
    }
}
```

### 권장

```java
@Transactional
public void signup(SignupRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
        throw new BusinessException(ErrorCode.USER_DUPLICATE_EMAIL);
    }

    // 회원가입 로직
}
```

---

## 4. GlobalExceptionHandler 역할

`GlobalExceptionHandler`는 Controller 또는 Service에서 발생한 예외를 한 곳에서 처리한다.

처리 흐름은 다음과 같다.

```text
Service에서 예외 발생
→ Controller 밖으로 예외 전파
→ GlobalExceptionHandler가 예외 처리
→ ErrorCode 기반 ErrorResponse 생성
→ ApiResponse.error(...)로 최종 응답 반환
```

---

## 5. GlobalExceptionHandler 처리 대상

현재 전역 예외 처리기는 다음 예외를 처리한다.

| 예외 | HTTP 상태 | 설명 |
|---|---:|---|
| `BusinessException` | `ErrorCode` 기준 | 직접 정의한 비즈니스 예외 |
| `MethodArgumentNotValidException` | 400 | `@Valid` 검증 실패 |
| `MethodArgumentTypeMismatchException` | 400 | PathVariable, RequestParam 타입 불일치 |
| `HttpRequestMethodNotSupportedException` | 405 | 지원하지 않는 HTTP 메서드 |
| `HttpMessageNotReadableException` | 400 | JSON 파싱 실패, 요청 본문 오류 |
| `AuthenticationException` | 401 | 로그인 인증 실패 |
| `AccessDeniedException` | 403 | Controller 이후 권한 없음 |
| `Exception` | 500 | 예상하지 못한 서버 오류 |

`Exception` 핸들러는 반드시 가장 마지막에 둔다.

---

## 6. @Valid 검증 실패 처리

요청 DTO에서 Bean Validation이 실패하면 `MethodArgumentNotValidException`이 발생한다.

요청 DTO 예시:

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

검증 실패 응답 예시:

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
      }
    ]
  }
}
```

---

## 7. 인증 실패 처리

현재 로그인 로직은 `AuthenticationManagerBuilder`를 사용한다.

로그인 과정에서 이메일 또는 비밀번호가 틀리면 `AuthenticationException` 계열 예외가 발생할 수 있다.

이 경우 전역 예외 처리기는 다음 에러 코드로 응답한다.

```java
ErrorCode.AUTH_INVALID_CREDENTIAL
```

응답 예시:

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

단, JWT 필터 단계에서 발생하는 401은 `GlobalExceptionHandler`가 아니라 Security 전용 EntryPoint에서 처리한다.

---

## 8. 권한 없음 처리

Controller 이후 단계에서 `AccessDeniedException`이 발생하면 `GlobalExceptionHandler`가 처리한다.

응답 예시:

```json
{
  "result": "ERROR",
  "error": {
    "code": "COMMON-403",
    "message": "권한이 없습니다.",
    "timestamp": "2026-05-05T18:10:22.123+09:00",
    "path": "/api/v1/admin/users"
  }
}
```

단, Spring Security Filter 단계의 403은 Security 전용 `AccessDeniedHandler`에서 처리한다.

---

## 9. 예상하지 못한 Exception 처리

예상하지 못한 모든 예외는 `Exception` 핸들러에서 처리한다.

```java
@ExceptionHandler(Exception.class)
public ResponseEntity<ApiResponse<Void>> handleException(
        Exception e,
        HttpServletRequest request
) {
    log.error("[UnhandledException] path={}", request.getRequestURI(), e);

    return ResponseEntity
            .status(ErrorCode.INTERNAL_ERROR.toHttpStatus())
            .body(ApiResponse.error(
                    ErrorResponse.of(
                            ErrorCode.INTERNAL_ERROR,
                            ErrorCode.INTERNAL_ERROR.getMessage(),
                            request.getRequestURI()
                    )
            ));
}
```

규칙:

```text
서버 로그에는 스택 트레이스를 남긴다.
클라이언트에게 내부 상세 메시지를 노출하지 않는다.
응답 메시지는 ErrorCode.INTERNAL_ERROR의 기본 메시지를 사용한다.
```

응답 예시:

```json
{
  "result": "ERROR",
  "error": {
    "code": "COMMON-500",
    "message": "서버 내부 오류입니다.",
    "timestamp": "2026-05-05T18:10:22.123+09:00",
    "path": "/api/v1/example"
  }
}
```

---

## 10. 사용 금지 규칙

### RuntimeException 직접 사용 금지

```java
// 금지
throw new RuntimeException("Email already exists");
```

```java
// 권장
throw new BusinessException(ErrorCode.USER_DUPLICATE_EMAIL);
```

---

### Controller에서 try-catch 금지

```java
// 금지
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    try {
        return ResponseEntity.ok(authService.login(request));
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
```

```java
// 권장
@PostMapping("/login")
public ResponseEntity<ApiResponse<AuthResponse>> login(
        @Valid @RequestBody LoginRequest request
) {
    AuthResponse response = authService.login(request);
    return ResponseEntity.ok(ApiResponse.success(response));
}
```

---

### Service에서 ApiResponse.error 직접 생성 금지

```java
// 금지
return ApiResponse.error(ErrorCode.USER_DUPLICATE_EMAIL);
```

```java
// 권장
throw new BusinessException(ErrorCode.USER_DUPLICATE_EMAIL);
```

---

## 11. 현재 적용 파일

```text
common
 ├── exception
 │    ├── BusinessException.java
 │    ├── ErrorCode.java
 │    └── GlobalExceptionHandler.java
 └── response
      ├── ApiResponse.java
      └── ErrorResponse.java
```

---

## 12. 다음 단계

이 예외 처리 계층을 만든 뒤에는 다음 작업을 진행한다.

```text
1. ForbiddenException, UnauthorizedException 생성
2. UserNotFoundException, DuplicateEmailException 생성
3. AuthController에 ApiResponse.success(...) 적용
4. LoginRequest, SignupRequest에 Bean Validation 추가
5. AuthService의 RuntimeException 제거
6. Security 401/403 전용 핸들러 추가
```
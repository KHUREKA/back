# Entity 공통 계층 컨벤션

## 1. 목적

이 문서는 KHUREKA 백엔드에서 JPA Entity를 만들 때 사용하는 공통 규칙을 정리한다.

현재 Entity 공통 계층은 다음 파일을 중심으로 구성한다.

```text
BaseEntity        → 생성 시간, 수정 시간 공통 관리
JpaAuditingConfig → JPA Auditing 활성화 설정
```

`BaseEntity`는 대부분의 Entity에서 반복적으로 필요한 생성 시간과 수정 시간을 공통으로 관리하기 위해 사용한다.

```text
createdAt → 데이터 최초 생성 시각
updatedAt → 데이터 마지막 수정 시각
```

---

## 2. BaseEntity 역할

`BaseEntity`는 모든 Entity에서 공통으로 사용할 수 있는 추상 클래스다.

```java
@Getter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
```

`BaseEntity` 자체는 테이블로 생성되지 않는다.

대신 `BaseEntity`를 상속한 Entity의 테이블에 다음 컬럼이 추가된다.

```text
created_at
updated_at
```

---

## 3. JPA Auditing 설정

`@CreatedDate`, `@LastModifiedDate`가 동작하려면 JPA Auditing을 활성화해야 한다.

이를 위해 다음 설정 클래스를 사용한다.

```java
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
```

이 설정이 있어야 Entity 저장 및 수정 시 시간이 자동으로 기록된다.

---

## 4. 사용 방법

새로운 Entity를 만들 때는 기본적으로 `BaseEntity`를 상속한다.

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

이 경우 `posts` 테이블에는 다음과 같은 컬럼이 포함된다.

```text
id
title
content
created_at
updated_at
```

---

## 5. 적용 기준

새로 만드는 대부분의 도메인 Entity는 `BaseEntity`를 상속한다.

예시:

```text
Post
Comment
Team
Reservation
Place
ChatRoom
Notification
```

다음과 같은 Entity에는 `BaseEntity` 상속을 우선 고려한다.

```text
생성 시각이 필요한 데이터
수정 시각이 필요한 데이터
목록 정렬에서 최신순/오래된순이 필요한 데이터
관리자 화면이나 디버깅에서 생성/수정 이력이 필요한 데이터
```

---

## 6. 기존 Entity 적용 주의사항

이미 존재하는 Entity에 `BaseEntity`를 적용하면 해당 테이블에 컬럼이 추가된다.

예를 들어 기존 `User` Entity에 `BaseEntity`를 상속하면 `users` 테이블에 다음 컬럼이 추가된다.

```text
created_at
updated_at
```

따라서 기존 Entity에 적용할 때는 반드시 다음을 확인한다.

```text
DB 컬럼 추가 영향이 있는지
기존 데이터에 null 문제가 생기지 않는지
해당 Entity 담당자와 협의했는지
팀원 로컬 DB에서 ddl-auto update로 정상 반영되는지
배포 환경에서는 마이그레이션 전략이 있는지
```

현재 단계에서는 `BaseEntity`와 `JpaAuditingConfig`만 추가하고, 기존 `User` Entity에는 바로 적용하지 않는다.

`User`는 인증/회원 도메인과 연결된 핵심 Entity이므로, 담당자와 협의 후 별도 PR에서 적용한다.

---

## 7. createdAt / updatedAt 사용 규칙

### 7.1 createdAt

`createdAt`은 Entity가 최초 저장될 때 자동으로 기록된다.

```text
INSERT 시 자동 저장
UPDATE 시 변경되지 않음
```

목록 조회에서 최신순 정렬이 필요할 때 사용할 수 있다.

```java
PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
```

---

### 7.2 updatedAt

`updatedAt`은 Entity가 저장되거나 수정될 때 자동으로 기록된다.

```text
INSERT 시 최초 저장
UPDATE 시 자동 갱신
```

수정 시간 기준 정렬이나 최근 변경 데이터 조회에 사용할 수 있다.

---

## 8. Entity 작성 규칙

Entity는 기본적으로 다음 규칙을 따른다.

```text
Entity는 class로 작성한다.
record를 Entity로 사용하지 않는다.
기본 생성자는 protected로 제한한다.
필드는 가능한 private로 둔다.
Setter는 기본적으로 만들지 않는다.
상태 변경은 의미 있는 메서드로 표현한다.
```

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

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
```

---

## 9. API 응답에서의 사용 규칙

Entity를 API 응답으로 직접 반환하지 않는다.

```java
// 금지
return ResponseEntity.ok(ApiResponse.success(post));
```

반드시 Response DTO로 변환해서 반환한다.

```java
// 권장
return ResponseEntity.ok(ApiResponse.success(PostResponse.from(post)));
```

`createdAt`, `updatedAt`을 응답에 포함할지는 API 요구사항에 따라 결정한다.

예시:

```java
@Getter
@Builder
public class PostResponse {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PostResponse from(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
```

---

## 10. 사용 금지 규칙

### 10.1 Entity에서 직접 시간 설정 금지

```java
// 금지
this.createdAt = LocalDateTime.now();
this.updatedAt = LocalDateTime.now();
```

생성/수정 시간은 JPA Auditing이 자동으로 관리하게 둔다.

---

### 10.2 모든 Entity에 무조건 적용하지 않기

모든 클래스에 무조건 `BaseEntity`를 상속하지 않는다.

다음과 같은 경우에는 적용하지 않을 수 있다.

```text
순수 값 객체
임베디드 타입
매핑 전용 중간 객체
생성/수정 시간이 의미 없는 테이블
```

---

### 10.3 기존 Entity에 무단 적용 금지

기존 Entity에 `BaseEntity`를 적용하면 DB 스키마가 변경된다.

따라서 기존 Entity에 적용할 때는 담당자와 협의 후 별도 PR로 진행한다.

---

## 11. 현재 적용 상태

이번 공통 계층 작업에서는 다음 파일만 추가한다.

```text
common
 └── entity
      └── BaseEntity.java

config
 └── JpaAuditingConfig.java
```

현재 PR에서는 기존 `User` Entity에 `BaseEntity`를 적용하지 않는다.

후속 작업에서 팀 합의 후 다음과 같이 적용할 수 있다.

```java
public class User extends BaseEntity {
    ...
}
```

---

## 12. 최종 사용 예시

Entity:

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

Service:

```java
@Transactional(readOnly = true)
public PageResponse<PostResponse> getPosts(Pageable pageable) {
    Page<Post> postPage = postRepository.findAll(pageable);
    Page<PostResponse> responsePage = postPage.map(PostResponse::from);

    return PageResponse.from(responsePage);
}
```

Response DTO:

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

응답:

```json
{
  "result": "SUCCESS",
  "data": {
    "content": [
      {
        "id": 1,
        "title": "첫 번째 글",
        "content": "내용",
        "createdAt": "2026-05-05T18:10:22"
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
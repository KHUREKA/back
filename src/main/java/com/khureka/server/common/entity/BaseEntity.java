package com.khureka.server.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 모든 Entity에서 공통으로 사용할 생성/수정 시간 관리용 상위 클래스.
 *
 * 사용 예시:
 *
 * @Entity
 * public class Post extends BaseEntity {
 *     ...
 * }
 *
 * 주의:
 * - 이 클래스 자체는 테이블로 생성되지 않는다.
 * - 이 클래스를 상속한 Entity의 테이블에 created_at, updated_at 컬럼이 추가된다.
 * - 기존 Entity에 적용할 때는 DB 컬럼 추가 영향이 있으므로 팀원과 협의 후 적용한다.
 */
@Getter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    /**
     * Entity 최초 생성 시각.
     *
     * INSERT 시 자동 저장된다.
     * 이후 UPDATE 시 변경되지 않는다.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Entity 마지막 수정 시각.
     *
     * INSERT 시 최초 저장되고,
     * UPDATE 시 자동 갱신된다.
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
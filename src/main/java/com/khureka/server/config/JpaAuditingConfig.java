package com.khureka.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing 설정 클래스.
 *
 * @CreatedDate, @LastModifiedDate가 동작하려면
 * @EnableJpaAuditing 설정이 필요하다.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
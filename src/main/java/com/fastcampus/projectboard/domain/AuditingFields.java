package com.fastcampus.projectboard.domain;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

// @MappedSuperclass
// 해당 annotation 의 class 는 자체적으로 entity 가 아니지만, 여러  entity class 에 공통적으로 사용되는 mapping 정보를 담울 수 있음.
@Getter
@ToString
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class AuditingFields {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @CreatedDate
    @Column(nullable = false, updatable = false)
    protected LocalDateTime createdAt;
    // protected -> 해당 class 는 대부분의 entity 들이 superclass 로 가지게 된다.
    // 이때 subclass 에서 해당 class field 에 접근하기 위해서는 접근 제어자가 protected 이여야 한다.
    // OAuth 구현 전까지는 기본적으로 이미 DB 에 저장된 UserAccount 정보만을 이용했으므로,
    // 인증 작업을 위해 runtime 중 해당 class field 에 직접 접근하는 일이 없었으나,
    // 이제 관련 상황이 발생할 것으로 예상되어짐.

    @CreatedBy
    @Column(nullable = false, updatable = false, length = 100)
    protected String createdBy;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @LastModifiedDate
    @Column(nullable = false)
    protected LocalDateTime modifiedAt;

    @LastModifiedBy
    @Column(nullable = false, length = 100)
    protected String modifiedBy;
}


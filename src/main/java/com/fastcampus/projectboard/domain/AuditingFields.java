package com.fastcampus.projectboard.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;


import java.time.LocalDateTime;

@Getter
@ToString
@EntityListeners(AuditingEntityListener.class)
// 엔티티 클래스에서 이벤트 처리하기 위한 리스너를 지정
// `AuditingEntityListener.class`가 지정 되어 있으므로 해당 클래스에서 정의된 엔티티 이벤트를 처리
// 일반적으로 이 애노테이션은 엔티티의 생성일 및 수정일과 같은 감사 정보를 처리하는 데 사용
@MappedSuperclass
// 부모 클래스가 엔티티가 아님을 나타낸다.
// 즉, 이 클래스는 데이터베이스에 매핑되지 않습니다.
// 하지만 이 클래스의 필드와 메서드는 자식 엔티티 클래스에 상속된다.
// 이러한 설정을 사용하면 공통 필드와 메서드를 부모 클래스에 정의할 수 있으므로 코드 중복을 줄일 수 있다.
public class AuditingFields {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    //Spring에서 날짜 및 시간 형식을 지정하는데 사용
    @CreatedDate
    //필드가 엔티티를 생성할 때 자동으로 설정되는 생성일을 나타낸다.
    @Column(nullable = false, updatable = false)
    //`updatable=false` : 해당 열의 값이 업데이트 되지 않음을 나타낸다.
    // 일반적으로 생성일은 엔티티가 생성될 때 설정되고 나중에 변경되지 않아야 하므로 이러한 설정이 필요
    private LocalDateTime createdAt; // 생성일시

    @CreatedBy
    @Column(nullable = false, updatable = false, length = 100)
    private String createdBy; // 생성자

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedAt; // 수정일시

    @LastModifiedBy
    @Column(nullable = false, length = 100)
    private String modifiedBy; // 수정자

}
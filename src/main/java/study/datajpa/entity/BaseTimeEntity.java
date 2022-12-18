package study.datajpa.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;


/**
 * 이걸 최상위로 만들어 이 데이터는 웬만하면 다 쓰니
 * 생성시간 수정시간만 필요하면 이거만
 * 다른기능도 필요하면 BaseEntity
 * @MappedSuperclass 로 공통기능 모아놔서 이 클래스 상속해서 쓰는
 *
 * @EntityListeners(AuditingEntityListener.class) 는
 * 이 클래스에 Auditing 기능 포함시킴
 */
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseTimeEntity {

    /** 엔티티 생성되어 저장될 떄 시간이 자동 저장 */
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    /** 조회한 엔티티의 갑을 변경할 떄 시간이 자동 저장 */
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}

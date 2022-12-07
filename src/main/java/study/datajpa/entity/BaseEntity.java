package study.datajpa.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * Auditing
 * 스프링 데이터 JPA 사용
 *
 * @EntityListeners(AuditingEntityListener.class) 를 생략하고 스프링 데이터 JPA 가 제공하는
 * 이벤트를 엔티티 전체에 적용하려면 orm.xml에 다음과 같이 등록하면 된다.
 * META-INF/orm.xml 생성해 코드 입력
 *
 * 이벤트 기반으로 동작한다는걸 넣어줘야함
 */
@EntityListeners(AuditingEntityListener.class)
/** 이거는 똑같이 적용해야함 */
@MappedSuperclass
@Getter
public class BaseEntity extends BaseTimeEntity{

    /**
     * 등록자, 수정자도 등록
     * 그냥 두면 값이 안들어가므로 스프링 설정 클래스에 bean으로 등록
     * */
    @CreatedBy
    /** 등록은 수정 못하게 false로 */
    @Column(updatable = false)
    private String createdBy;

    /** By는 누군가에 의해서 */
    @LastModifiedBy
    private String lastModifiedBy;

    /** 실무에서 대부분의 엔티티는 등록시간, 수정시간이 필요하지만, 등록자, 수정자는 없을 수도 있다.
    //그래서 이 클래스에서는 등록일 수정일만 작성하고 이 클래스 상속받는 클래스에 등록자 수정자 작성하는 경우도 있음 */
}

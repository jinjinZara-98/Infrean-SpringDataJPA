package study.datajpa.entity;

import lombok.Getter;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

/**
 * 생성시간 수정시간 알기위해
 * Auditing
 * 순수 JPA 사용
 *
 * 진짜 상속관계가 아닌 속성들 그냥 내려서 테이블에서 같이 쓸 수 있게 하는
 * 이걸 상속한 member테이블 실행하면 등록과 수정 시간 열에 추가되지 않음
 *
 *  @MappedSuperclass를 추가해줘야 시간들 열에 추가됨,
 *
 *    공통 속성들 모아서 처리할 때 이 클래스 상속하는
*/
@MappedSuperclass
@Getter
public class JpaBaseEntity {

    /** 값을 혹시 실수라도 바뀌어도 db에 변경이 되지 않게 */
    @Column(updatable = false)
    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    /**
     * JPA 주요 이벤트 어노테이션
     * @PrePersist, @PostPersist
     * @PreUpdate, @PostUpdate
     *
     * */

    /** 퍼시스트 하기전에 실행 */
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        //데이터를 넣어놔야 쿼리 날릴때 편함, 등록일 수정일 맞추는
        createdDate = now;
        updatedDate = now;
    }

    /**
     * 업데이트 하기 전
     * 즉 이 클래스를 상속받은 엔티티 객체는 값이 변경되면
     * 변경된 시점을 db에 저장하기 전에 updatedDate 칼럼에 넣어준다?
     * */
    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }
}
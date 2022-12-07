package study.datajpa.entity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import java.time.LocalDateTime;

/** 새로운 엔티티를 구별하기 위한 예제 */
@Entity
/** 이벤트 기반으로 동작한다는걸 넣어줘야함 */
@EntityListeners(AuditingEntityListener.class)
//jpa는 기본생성자 필요, 롬복으로 어노테이션으로 기본생성자 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED)
/**
 * Persistable 인터페이스를 구현해서 판단 로직 변경 가능
 * @GenerateValue 어노테이션은 영속될 때 자동으로 기본키값 생성
 * @GenerateValue 안 쓰고 직접 기본키 값 설정하고 save() 호출하면 병헙이 적용됨
 * 병합은 해당 엔티티가 데이터베이스에 있는지 select 쿼리로 확인하고 없으면 저장하기 때문에 비효율적
 * @GenerateValue 안 쓰고 직접 기본키 값 설정할 때 병합이 적용되지 않게 하는 예제
 *  */
public class Item implements Persistable<String> {

    /**
     * JPA 식별자 생성 전략이 @GenerateValue 면 save() 호출 시점에 식별자가 없으므로 새로운 엔티티로 인식해서 정상 동작한다.
     *
     * @GenerateValue는 JPA에 퍼시스트 하면 생김. 때문에 save() 호출 시점에 식별자가 없는걸로 판단
     * 그런데 JPA 식별자 생성 전략이 @Id 만 사용해서 기본키를 직접 할당이면 이미 식별자 값이 있는 상태로 save() 를 호출한다.
     * 따라서 이 경우 merge() 가 호출된다. merge() 는 우선 DB를 호출해서 값을 확인하고,
     * DB에 값이 없으면 새로운 엔티티로 인지하므로 매우 비효율 적이다.
     *
     * 따라서 Persistable 를 사용해서 새로운 엔티티 확인 여부를 직접 구현하게는 효과적이다.
     * 참고로 등록시간( @CreatedDate )을 조합해서 사용하면 이 필드로 새로운 엔티티 여부를 편리하게 확인할 수 있다.
     * (@CreatedDate에 값이 없으면 새로운 엔티티로 판단)
     * */
    @Id
    private String id;

    //jpa에 persist전에 호출
    @CreatedDate
    private LocalDateTime createdDate;

    public Item(String id) {
        this.id = id;
    }

    //Persistable상속받아 만든 메서드
    @Override
    public String getId() {
        return id;
    }

    /**
     * Persistable 메서드 오버라이딩
     *
     * 새로운 엔티티인지 확인하는걸 직접 작성
     * Persistable인터페이스 가지고 있으면 이거로 판단하는
     * Persistable 상속받아 만든 메서드
     * 새거가 아닌지 맞는지 로직 짜는
     * */
    @Override
    public boolean isNew() {
        /**
         * 생성날짜가 없으면 새로운 객체다, 값이 있으면 jpa를 통해 들어간것이니
         * 생성날짜가 있다는건 그 전에 이 객체가 들어갔다는것
         * */
        return createdDate == null;
    }
}
/**
 * 명세
 * where문에서 or and를 조립해서 쓸수있는,
 * 스프링 데이터 JPA는 JPA Criteria를 활용해서 이 개념을 사용할 수 있도록 지원
 * Criteria는 쓰기 힘듬
 * */

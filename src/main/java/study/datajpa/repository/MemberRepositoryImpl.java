package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

/** 실무에서는 주로 QueryDSL이나 SpringJdbcTemplate을 함께 사용할 때
//사용자 정의 리포지토리 기능 자주 사용
//항상 사용자 정의 리포지토리가 필요한 것은 아니다. 그냥 임의의 리포지토리를 만들어도 된다.
//예를들어 MemberQueryRepository를 인터페이스가 아닌 클래스로 만들고 스프링 빈으로 등록해서
//그냥 직접 사용해도 된다. 물론 이 경우 스프링 데이터 JPA와는 아무런 관계 없이 별도로 동작한다

//개발할때 분리를 해야한느지 고민, 커맨드랑 쿼리 분리, 핵심 비즈니스 로직 분리

//MemberRepositoryCustom 구현한 클래스
//MemberRepository가 MemberRepositoryCustom 상속받고 있고 MemberRepositoryCustom의 구현체가 이 클래스
//MemberRepositoryCustom의 메서드 findMemberCustom을 여기서 구현, 실행하면 여기 있는 findMemberCustom가 실행
//자바가 아닌 스프링 데이터 JPA가 지원해주는

//EntityManager파라미터로 주입받는 생성자 생성 */
@RequiredArgsConstructor
/** 상속하는 인터페이스 이름은 뭘로 맞춰도 상관없지만 MemberRepositoryImpl는 MemberRepository랑 Impl꼭 맞춰줘야함
//그래야 findMemberCustom 호출하면 여기있는걸 호출
//규칙: 리포지토리 인터페이스 이름 + Impl
//스프링 데이터 JPA가 인식해서 스프링 빈으로 등록 */
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    //하나만 있으면 자동의존주입 해줌 @Autowired없어도
    private final EntityManager em;

    //순수한 jpa쓰는
    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}
//운영할때 힘듬, 언제 등록된지 모르면 로그를 뒤져야해서
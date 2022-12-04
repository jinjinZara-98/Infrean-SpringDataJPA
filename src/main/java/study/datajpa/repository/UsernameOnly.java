package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

/**
 * Projections
 * 회원 이름만 가져오는
 * 구현클래스는 스프링이 만들어줌
 * 인터페이스만 정의하면 됨
 * */
public interface UsernameOnly {

    /** 인터페이스 기반 Open Proejctions
     * 다음과 같이 스프링의 SpEL 문법도 지원
     * target은 Member, username와 age 갖고와 문장에 넣어준다
     * 단! 이렇게 SpEL문법을 사용하면, DB에서 엔티티 필드를 다 조회해온 다음에 계산한다!
     * 따라서 JPQL SELECT 절 최적화가 안된다.
     * member데이터 다 가져와 spl계산, 결과 나온거에서 원하는 데이터 찍어서
     * 다 가져와서 처리해서 오픈 프로젝션 */

    @Value("#{target.username + ' ' + target.age + ' ' + target.team.name}")
    /** 프로퍼티명 */
    String getUsername();
}

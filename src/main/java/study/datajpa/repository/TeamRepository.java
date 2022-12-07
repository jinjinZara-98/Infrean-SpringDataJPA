package study.datajpa.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import study.datajpa.entity.Team;

import java.util.List;

/**
 * 타입과 pk값
 * @Repository 애노테이션 생략 가능
 * 컴포넌트 스캔을 스프링 데이터 JPA가 자동으로 처리
 *
 * JPA 포함해서 다른 데이터베이스 연동 기술을 사용하다가 발생하는 예외는 서로 다름
 * @Repository 는 스프링에서 사용할 수 있는 예외로 변경해줌
 * 그래서 서비스나 컨트롤러 게층으로 예외를 넘길 떄 각기 다른 데이터베이스 연동 기술에서 발생한 예외가
 * 스프링이 제공하는 예외로 변경
 * 결과적으로 예외를 처리하는 매커니즘은 동일
 *
 * 스프링 데이터 JPA 를 쓸 때 모두 @Transactional 걸려잇음
 */
public interface TeamRepository extends JpaRepository<Team, Long> {

    @Query("select distinct t from Team t join fetch t.members")
    List<Team> findTeamFetchJoin();

    @EntityGraph(attributePaths = {"members"})
    @Query("select distinct t from Team t")
    List<Team> findTeamEntityGraph();
}

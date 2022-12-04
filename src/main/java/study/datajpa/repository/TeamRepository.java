package study.datajpa.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import study.datajpa.entity.Team;

import java.util.List;

//타입과 pk값
//@Repository 애노테이션 생략 가능
//컴포넌트 스캔을 스프링 데이터 JPA가 자동으로 처리
//JPA 예외를 스프링 예외로 변환하는 과정도 자동으로 처리
public interface TeamRepository extends JpaRepository<Team, Long> {

    @Query("select distinct t from Team t join fetch t.members")
    List<Team> findTeamFetchJoin();

    @EntityGraph(attributePaths = {"members"})
    @Query("select distinct t from Team t")
    List<Team> findTeamEntityGraph();
}

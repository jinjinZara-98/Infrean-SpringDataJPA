package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

/**
 * 순수 JPA
 * 변경은 업데이트 메서드 필요가 없음, 컬렉션처럼 바꾸는거처럼
 * 엔티티매니저로 조회한다음 직접 수정하고 커밋하면 자동으로 변경된거 인지해 업데이트쿼리 날림
 *
 * @Repository의 기능이 더 있음, JPA의 예외를 스프링이 공통적으로 처리할 수 있는 예외로 변환하는 기능까지
 */
@Repository
public class TeamJpaRepositrory {

    //jpa의 엔티티매니저를 자동주입하는
    @PersistenceContext
    private EntityManager em;

    public Team save(Team team) {
        em.persist(team);

        return team;
    }

    public void deleted(Team team) {
        em.remove(team);
    }

    public Team find(Long id) {
        return em.find(Team.class, id);
    }

    public Optional<Team> findById(Long id) {
        Team team = em.find(Team.class, id);

        return Optional.ofNullable(team);
    }

    public Long count() {
        return em.createQuery("select count(t) from Team t", Long.class)
                .getSingleResult();

    }

    public List<Team> findAll() {
        return em.createQuery("select t from Team t", Team.class)
                .getResultList();
    }
}

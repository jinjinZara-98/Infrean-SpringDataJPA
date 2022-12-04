package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

/**순수 JPA

//변경은 업데이트 메서드 필요가 없음, 컬렉션처럼 바꾸는거처럼
//엔티티매니저로 조회한다음 직접 수정하고 커밋하면 자동으로 변경된거 인지해 업데이트쿼리 날림

//@Repository의 기능이 더 있음, JPA의 예외를 스프링이 공통적으로 처리할 수 있는 예외로 변환하는 기능까지 */
@Repository
public class MemberJpaRepository {

    //jpa를 쓰려면 EntityManager 잇어야함
    //jpa에 있는 영컨, 여기다 값 집어넣으면 db에 넣어줌
    @PersistenceContext
    private EntityManager em;

    //저장하고 저장하는 객체 반환
    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    //삭제, 엔티티객체에 remove메서드하면 실제 db에서 삭제쿼리 나감
    public void delete(Member member) {
        em.remove(member);
    }

    //전체를 조회하거나 where조건을 넣으면 jpql을 써야함
    //Member는 테이블이 아니라 엔티티
    //쿼리 실행되고 jpql이 sql로 번역되 데이터를 가져옴
    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    //Optional로 조회하는, null일수도 있고 아닏ㄹ 수도 있고
    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);

        return Optional.ofNullable(member);
    }

    //개수 조회, getResultList()는 결과 하나
    public Long count() {
        return em.createQuery("select count(m) from Member m", Long.class)
                .getSingleResult();
    }

    //조회
    public Member find(Long id) {
        return em.find(Member.class, id);
    }

    //파라미터로 넣은 나이보다 많은 회원 리스트
    public List<Member> findByUsernameAndAgeGreaterThan(String username, int age) {
        return em.createQuery("select m from Member m where m.username = :username and m.age > :age")
                .setParameter("username", username)
                .setParameter("age", age)
                .getResultList();
    }

    //JPA를 직접 사용해서 Named 쿼리 호출, Member클래스에서 Member.findByUsername이름으로 정의해놓음
    public List<Member> findByUsername(String username) {

        return em.createNamedQuery("Member.findByUsername", Member.class)
                        .setParameter("username", username)
                        .getResultList();
    }

    //많은 데이터를 한번에 어플리케이션에 다 갖고오는건 불가, 적당히 끊어서
    //몇번째부터 몇번째까지 가져와라
    //setFirstResult는 어디서부터 가져올꺼냐, setMaxResults는 개수를 몇개 가져올꺼냐
    public List<Member> findByPage(int age, int offset, int limit) {
        return em.createQuery("select m from Member m where m.age = :age order by m.username desc")
                        .setParameter("age", age)
                        .setFirstResult(offset)
                        .setMaxResults(limit)
                        .getResultList();
    }

    //순수 JPA 페이징과 정렬
    //내 페이지는 몇번째야, 카운트이기 때문에 getSingleResult
    public long totalCount(int age) {
        return em.createQuery("select count(m) from Member m where m.age = :age",
                Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }

    /**
     * 순수 JPA 벌크 쿼리, 전체 값을 수정하는
     * 파라미터로 넘어온 나이보다 크거나 같으면 수정, .executeUpdate() 해야 개수가 나옴
     */
    public int bulkAgePlus(int age) {
        int resultCount = em.createQuery(
                "update Member m set m.age = m.age + 1" +
                        "where m.age >= :age")
                .setParameter("age", age)
                .executeUpdate();
        return resultCount;
    }
}

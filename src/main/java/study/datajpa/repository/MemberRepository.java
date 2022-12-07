package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


/**
 * @Repository 애노테이션 생략 가능
 * 컴포넌트 스캔을 스프링 데이터 JPA가 자동으로 처리
 * JPA 예외를 스프링 예외로 변환하는 과정도 자동으로 처리
 *
 * 주의
 * T findOne(ID) Optional<T> findById(ID) 변경
 * 제네릭 타입
 * T : 엔티티
 * ID : 엔티티의 식별자 타입
 * S : 엔티티와 그 자식 타입
 *
 * 주요 메서드
 * save(S) : 새로운 엔티티는 저장하고 이미 있는 엔티티는 병합한다. merge라는 기능도 같이
 * delete(T) : 엔티티 하나를 삭제한다. 내부에서 EntityManager.remove() 호출
 * findById(ID) : 엔티티 하나를 조회한다. 내부에서 EntityManager.find() 호출
 * getOne(ID) : 엔티티를 프록시로 조회한다. 내부에서 EntityManager.getReference() 호출
 * 가짜 프록시 객체 찾아오는, 프록시를 실제 내가 원할때 값을 건드려 안에 있는 값을 꺼낼때 db에 쿼리 나가서 프록시 초기화
 * findAll(…) : 모든 엔티티를 조회한다. 정렬( Sort )이나 페이징( Pageable ) 조건을 파라미터로 제공할 수있다.
 * 참고: JpaRepository 는 대부분의 공통 메서드를 제공한다.
 * JpaRepository PagingAndSortingRepository CrudRepository Repository 순
 *
 * 엔티티 타입과 pk값
 * */
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    /**
     * 공통기능 말고 다른 기능 메서드를 만들고 이 인터페이스를 구현하면 공통기능메서드까지 다 상속받아야함
     *
     * 그 기능만 구현하는게 커스텀기능, 사실 구현하지 않아도 동작? 쿼리메서드 기능
     *
     * 스프링 데이터 JPA는 메소드 이름을 분석해서 JPQL을 생성하고 실행
     * 메서드 이름에서 Username And Age하면 and조건으로 먹임 Username은 그냥 equal조건, GreaterThan는 이거보다 크면
     * 이름 조금이라도 다르게하면 기능 실행 안됨
     * */
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    /**조회: find … By ,read … By ,query … By get … By,
     *
     * 예:) findHelloBy 처럼 ...에 식별하기 위한 내용(설명)이 들어가도 된다.
     * COUNT: count…By 반환타입 long
     * EXISTS: exists…By 반환타입 boolean
     * 삭제: delete…By, remove…By 반환타입 long
     * DISTINCT: findDistinct, findMemberDistinctBy
     * LIMIT: findFirst3, findFirst, findTop, findTop3 첫번째부터 3개, 위에서 3개
     *
     * 이 기능은 엔티티의 필드명이 변경되면 인터페이스에 정의한 메서드 이름도 꼭 함께 변경해야 한다.
     * 그렇지 않으면 애플리케이션을 시작하는 시점에 오류가 발생한다.
     * 이렇게 애플리케이션 로딩 시점에 오류를 인지할 수 있는 것이 스프링 데이터 JPA의 매우 큰 장점
     *
     * 파라미터 없으면 조건 주지 않아 모든 Member조회
     * */
    List<Member> findHelloBy();

    /**
     * namedQuery를 편하게 호출하는
     *
     * 쿼리에서 username이라는 파라미터에 값을 넣음
     *
     * @Param("username")는 파라미터 바인딩,
     * 이름 기반임, 위치 기반은 위치가 바뀌면 에러 때문에 잘 사용 안함
     * 반환타입이 Member. 하고 현재 메서드이름을 붙인 같은 이름을 갖는 namedQuery가 있다면 @Query안붙여줘도됨
     * 만약 메서드이름으로 namedQuery 못찾으면 메서드 이름으로 쿼리문을 만듬
     */
    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    /**
     * jpql을 다른 곳이 아닌 인터페이스에 직접 정의하는, 실행할 메서드에 정적 쿼리를 직접 작성, 실무에서 많이 씀
     *
     * 이름이 없는 namerdQuery라고 보면 됨, namerdQuery의 장점인 컴파일 오류를 사용할 수 있음
     * 이름이 없는 namedQuery라고 봐도 무방
     * 메서드이름으로 쿼리 만드는건 파라미터가 많아지면 메서드이름이 너무 길어짐
     * 실무에서는 간단한 쿼리는 메서드이름으로 쿼리를 사용하고, 파라미터가 많아지면 메서드 이름을 간단하게 정의하고 이렇게 JPQL 직접
     * */
    @Query("select m from Member m where m.username= :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    /**@Query, 단순히 값 하나(컬럼 하나)를 조회, 회원의 이름들을 다 조회 */
    @Query("select m.username from Member m")
    List<String> findUsernameList();

    /**
     * DTO로 조회하는
     *
     * 팀도 가져오므로 조인 DTO로 조회할때는 뉴 오퍼레이션, 패키지 다 적어줘야함
     * 쿼리에서 나온 select결과를 dto객체를 생성자로 생성해 파라미터로 전달해줌
     * */
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) " +
            "from Member m join m.team t")
    List<MemberDto> findMemberDto();

    /**
     * 컬렉션 파라미터 바인딩, in절로 여러 개를 조회하고 싶을때 사용
     *
     * in절에 괄호 쉼표는 자동으로 처리됨
     * 다른 것들도 받을 수 있게 List가 아닌 Collection으로 받음
     * */
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    /** find By 사이에 어떤게 와도 상관없음, By 뒤에는 equal */
    List<Member> findListByUsername(String name); //컬렉션
    Member findMemberByUsername(String name); //단건
    Optional<Member> findOptionalByUsername(String name); //단건 Optional

    /**
     * 스프링 데이터 jpa가 페이징을 표준화시킴, 공통화
     *
     * sort와 pagable로 통일시킴
     * org.springframework.data.domain.Page : 추가 count 쿼리 결과를 포함하는 페이징,
     * contents와 totalcount쿼리 같이 나감
     * org.springframework.data.domain.Slice : 추가 count 쿼리 없이 다음 페이지만 확인 가능 ,ex)더보기
     * contents만 +!해서
     * (내부적으로 limit + 1조회)
     * List (자바 컬렉션): 추가 count 쿼리 없이 결과만 반환
     *
     * 메서드 이름으로 쿼리 만듬, Pageable은 인터페이스
     * 반환타입을 Page라고 하고 파라미터로 Pageable을 넣으면 쿼리에 대한 조건
     * Pageable은 몇페이지다 하는 정보
     * */
    Page<Member> findByAge (int age, Pageable pageable); //count 쿼리 사용
    /**
     * count 쿼리 사용안함, totalpage 안갖고옴, 다음 페이지가 있냐 없냐 이 정도 기능만
     * 설정해놓은 size 즉 limit을 하나 더 늘려 쿼리 실행함, 다음 페이지 유무를 알 수 있다
     * */
//    Slice<Member> findByUsername(int age, Pageable pageable);
//    List<Member> findByAge (int age, Pageable pageable); //count 쿼리 사용안함
//    List<Member> findByAge (String name, Sort sort);

    /**
     * count 쿼리를 다음과 같이 분리할 수 있음, total이 제일 시간 많이 잡아먹으므로
     * 쿼리가 복잡해지면 분리하는게 좋다
     * */
    @Query(value = "select m from Member m left join m.team t",
           countQuery = "select count(m.username) from Member m")
    Page<Member> findByAge2 (int age, Pageable pageable);

    List<Member> findTop2ByAge(int age);
    /**
     * 스프링 데이터 JPA를 사용한 벌크성 수정 쿼리, 대량 데이터 수정
     * 파라미터에 해당하는 나이를 모두 1씩 증가
     * @Modifying가 있어야 개수가 나오는 .executeUpdate()가 실행되므로
     * 없으면 .getSingleResult .ResultList 같은걸 실행
     * 벌크연산은 영컨 거치지 않고 바로 db에 반영해주므로 clearAutomatically = true를 해줘야
     * 이렇게 하면 영컨에도 반영 바로 되는
     * 벌크 연산 후 바로 조회해야 할 떄 사용하는
     * */
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    /** 페치 조인 */
    @Query("select m from Member m join fetch m.team")
    List<Member> findMemberFetchJoin();

    /**findAll은 상위에 있으므로 오버라이딩 */
    @Override
    /**
     * Member랑 Team 같이 조회, JPQL 없이 페치 조인
     * N+1 문제 해결
     * 페치조인은 연관관계가 있는걸 한 번에 다 갖고오는, 즉시로딩처럼
     * select절에 데이터를 다 넣어주는
     * */
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    /**
     * JPQL + 엔티티 그래프
     * JPQL도 짜고 @EntityGraph 도 하고,
     * findAll처럼 기본으로 주어진 메서드 이름을 사용하는게 아니니 쿼리짜야됨
     * */
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    /**
     * 메서드 이름으로 쿼리에서 특히 편리하다.
     * 간단할때는 이거 쓰고 아니면 jpql에서 패치조인 사용
     * findEntityGraphBy find 와 By 사이에는 아무 값이나 들어가도 상관없음
     */
    @EntityGraph(attributePaths = ("team"))
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    /**
     * NamedEntityGraph 사용
     * 네임드쿼리처럼 Member클래스에 미리 @EntityGraph 정의해서
     * */
    @EntityGraph(value = "Member.all")
    @Query("select m from Member m")
    List<Member> findMemberNamedEntityGraph();

    /**
     * JPA는 인터페이스 모음, 구현체인 하이버네이트가 더 많은 기능을 쓰고 싶을때 힌트를 날려줌
     * 오직 조회용으로만
     * 이 메서드로 조회한 엔티티는 값을 변경해도 변경감지가 안일어난다, 조회용이니까
     * 크게 쓸 일 없다?
     * 진짜 중요하고 트래픽 많은 데에 넣는
     * */
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    /**
     * JPA를 통해 lock을 어노테이션을 통해 편리하게 사용할 수 있다, 쓸 일이 많진 않음
     * 실시간 트래픽이 많은 서비스에선 걸면 안됨
     * select 할때 다른 걸 손대지 못하게 락을 건다
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String name);

    /**
     * Projections
     *
     * 회원의 이름만 조회하는데 편하게 조회하는, 쿼리 select절에 들어갈 데이터,
     * 엔티티 대신에 DTO를 편리하게 조회할 때 사용
     * 전체 엔티티가 아니라 만약 회원 이름만 딱 조회하고 싶으면
     *
     * dto도 만들었음으로 UsernameOnlyDto를 제네릭에 넣어도됨
     * UsernameOnly는 인터페이스지만 구현체를 스프링이 프록시를 이용해 만듬
     * UsernameOnlyDto는 직접 구현체 만든
     * */
    List<UsernameOnly> findProjectionsByUsername(String username);

    /**
     * 이렇게 제네릭으로 주고 파라미터를 타입으로 받을 수도 있음
     *컬럼을 갖고오는게 다를 수도 있으니
     * */
    <T> List<T> findProjectionsByUsername(@Param("username") String username, Class<T> type);

    /**
     * 동적 Projections
     *
     * 다음과 같이 Generic type을 주면, 동적으로 프로젝션 데이터 번경 가능
     * 타입을 넘기면 갖고오고 싶은 값이 달라질때 타입만 넣어주면 됨, 쿼리는 같을 때
     * <T> List<T> findProjectionsByUsername(String username, Class<T> type);
     * List<UsernameOnly> result = memberRepository.findProjectionsByUsername("m1",sernameOnly.class);
     *
     * 네이티브쿼리
     * 가급적 네이티브 쿼리는 사용하지 않는게 좋음, 정말 어쩔 수 없을 때 사용
     * 최근에 나온 궁극의 방법 스프링 데이터 Projections 활용
     *
     * 스프링 데이터 JPA 기반 네이티브 쿼리
     * 페이징 지원
     *
     * 반환 타입
     *
     * Object[]
     * Tuple
     * DTO(스프링 데이터 인터페이스 Projections 지원)
     * 반환타입 몇가지가 지원이 안됨
     *
     * 제약
     * Sort 파라미터를 통한 정렬이 정상 동작하지 않을 수 있음(믿지 말고 직접 처리)
     * JPQL처럼 애플리케이션 로딩 시점에 문법 확인 불가
     * 동적 쿼리 불가
     * 네이티브 SQL을 DTO로 조회할 때는 JdbcTemplate or myBatis 권장
     * */
    @Query(value = "select * from member where username = ?", nativeQuery = true)
    Member findByNativeQuery(String username);

    /**
     *  dto를 뽑는데 편하게 뽑고 네이티브쿼리고 동적인 쿼리는 아닌
     * 네이티브 쿼리이기 때문에 카운트쿼리 별도로 작성
     * */
    @Query(value = "SELECT m.member_id as id, m.username, t.name as teamName " +
            "FROM member m left join team t",
            countQuery = "SELECT count(*) from member", nativeQuery = true)
    //정적쿼리를 네이티브로 짤때는 projection기능으로 해결할수있다 매칭을 잘하면
    Page<MemberProjection> findByNativeProjection(Pageable pageable);

    /**
     * 동적 네이티브 쿼리
     * 하이버네이트를 직접 활용
     * 스프링 JdbcTemplate, myBatis, jooq같은 외부 라이브러리 사용
     */
}

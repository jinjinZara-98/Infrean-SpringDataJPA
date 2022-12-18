package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

//스프링 데이터 JPA테스트
@SpringBootTest
@Transactional
//@Rollback(false)
public class MemberRepositoryTest {

    //MemberRepository는 인터페이스, 프록시 객체임
    //자동의존주입할때 스프링이 구현클래스를 만들어 꽃아줌
    //개발자는 JpaRepository를 상속받은 인터페이스만 만들어주면 됨
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    //같은 트랜잭션이면 같은 엔티티매니저를 불러 동작 위 repossitory도 같이 씀
    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        //스프링 데이터 JPA 메서드로 사용
        //getId()는 optional로 제공, get()은 optional에 있는거 깜
        Member savedMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member); //JPA 엔티티 동일성 보장
    }

    @Test
    public void testTeam() {
        //given
        //member1 -> teamA
        //member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        memberRepository.save(new Member("홍길동", 20, teamA));
        memberRepository.save(new Member("심봉사", 20, teamA));
        memberRepository.save(new Member("임꺽정", 20, teamB));

        List<Team> teams = teamRepository.findTeamFetchJoin();
//
//        for (Team team : teams) {
//            System.out.println("프록시 객체 " + team.getMembers().getClass());
//            for (Member member : team.getMembers()) {
//                System.out.println("회원 " + member);
//            }
//        }
//
        System.out.println("조회한 Team 개수 " + teams.size());

        for (Team team : teams) {
            //여기서 추가 쿼리 날라가는, N+1 문제 발생
            //일대다 컬렉션 패치 조인이나
            List<Member> members = team.getMembers();

            for (Member member : members) {
                System.out.println("팀 이름 = " + team.getName() + " 회원 이름 = " + member.getUsername());
            }
            System.out.println();
        }
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //엔티티에 대한 변경은 더티체킹 변경감지로
        //값 수정하고 테스트 끝나면 업데이트 쿼리를 커밋함
        findMember1.setUsername("member1!!!");

        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        Long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        memberRepository.delete(member1);
        memberRepository.delete(member2);

        Long deletecount = memberRepository.count();
        assertThat(count).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result =
                memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        //첫번째 행에서 이름과 나이 얻어와 같은지 비교
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    //MemberRepository인터페이스에서 @Query로 이용한거 테스트
    @Test
    public void testNamedQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    //jpql을 인터페이스에 직접 정의한거 테스트
    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    //@Query, 단순히 값 하나를 조회, 회원의 이름들을 다 조회
    @Test
    public void findUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }
    }

    //DTO로 조회하는
    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    //컬렉션 파라미터 바인딩, in절로 여러 개를 조회하고 싶을때 사용
    @Test
    public void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        //asList로 배열을 List로 바꿔 AAA BBB를 포함하는 회원들을 불러오는
        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA","BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }
    
    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        //리스트로 안받아도됨, 리스트로 받을때 해당하는 값이 없어도 빈리스트 반환
        //리스트로 안받을때 해당하는 값이 없으면 null, null이 넘어오는게 나음
        //데이터 있을지 없을지 모르면 Optional로, Optional은 비어있으면 Optional.emptu나옴
        //Optional인데 결과가 2개이상이면 예외 터짐

        //단건으로 지정한 메서드를 호출하면 스프링 데이터 JPA는 내부에서 JPQL의
        //Query.getSingleResult() 메서드를 호출한다. 이 메서드를 호출했을 때 조회 결과가 없으면
        //javax.persistence.NoResultException 예외가 발생하는데 개발자 입장에서 다루기가 상당히
        //불편하다. 스프링 데이터 JPA는 단건을 조회할 때 이 예외가 발생하면 예외를 무시하고 대신에 null을 반환
        List<Member> aaa = memberRepository.findListByUsername("AAA");
    }

    //스프링 데이터 JPA 페이징과 정렬
    @Test
    public void paging() {
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",10));
        memberRepository.save(new Member("member3",10));
        memberRepository.save(new Member("member4",10));
        memberRepository.save(new Member("member5",10));

        int age = 10;
        //스프링 데이터 JPA는 페이징 0부터 시작, 0페이지에서 3개 갖고와, 정렬조건, 정렬 대상
        //이것도 offset 0이어서 콘솔에 생랴됨
        //0이면 결과 offset 생략
        PageRequest pageRequest = PageRequest.of(0,3, Sort.by(Sort.Direction.DESC ,"username"));

        //인터페이스를 구현하지 않아도 PageRequest만 넘기면 됨
        //반환타입이 Page면 totalCount까지 같이 날려 코드 만들 필요 없음
        //그러므로 반환타입에 따라 토탈쿼리 날리지 말지 결정
        //메서드를 만들 때 pageable인터페이스를 구현한걸 넘기는데 보통 PageRequest를 많이 씀
        //반환타입 List도 가능, 대신 밑에 검증 메서드들 사용못함, 그냥 몇개만 꺼내오기 위한 용도`
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

//        List<Member> list = memberRepository.findTop2ByAge(age);

        //API인 경우에 그냥 반환하면 안되므로 DTO로 변환해 반환, 엔티티는 반환하면 안됨
        //page가 Member를 감싸고 있기 때문에 map은 내부의 것을 바꿔서 다른 결과를 내는
        Page<MemberDto> dtoPage = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        //getContent() 내부에 있는 값 3개를 꺼내고 싶으면
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        for (Member member : content) {
            //desc니까 큰거부터 3개
            System.out.println("member = " + member);

        }
        //5개 뽑아옴
        System.out.println("totalElements = " + totalElements);

        assertThat(content.size()).isEqualTo(3);
        //slice면 이 기능 사용 못함, 토탈쿼리를 안날리므로
        assertThat(page.getTotalElements()).isEqualTo(5);
        //페이지 번호 갖고옴
        assertThat(page.getNumber()).isEqualTo(0);
        //0부타 3개까지 뽑으니 0~3 1페이지 4~5 2페이지
        //slice면 이 기능 사용 못함
        assertThat(page.getTotalPages()).isEqualTo(2);
        //첫번쨰 페이지인지
        assertThat(page.isFirst()).isTrue();
        //다음 페지이 유무
        assertThat(page.hasNext()).isTrue();

        //슬라이스는 토탈카운트 가져오지 않고 다음페이지 유무만 판단, 1개를 더 더해서 카운트쿼리 날려서 가져오는
    }

    //벌크연산은 영속성 컨텍스트의 엔티티 관리를 무시하고 db에 바로 쿼리를 날림
    //밑에 member 들 값이 영속성 컨텍스트에 들어있는게 데이터베이스에는 반영이 되지 않음
    //벌크 연산은 영속성 컨텍스트를 무시하고 바로 데이터베이스에 반영
    //이렇게 되면 영속성 컨텍스트의 값과 데이터베이스 값과 맞지 않다
    //그래서 벌크 연산후 영컨을 다 비워야함
    @Test
    public void bulkUpdate() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        //스프링 데이터 jpa의 save를 하면 jpql 을 실행하므로 bulkAgePlus() 메서드 실행하기 전에
        //데이터를 db에 반영 flush함, 즉 업데이트하는 쿼리가 있으면
        //영속성 컨텍스트에 잇는 값들 flush함, 하지만 clear는 무조건 해줘야함
        //@Modifying(clearAutomatically = true)로 해주면 자동으로 clear해줌
        //jdbc템플릿이나 마이바티스 쓸때도 조심, 똑같이 flush나 clear 해주고
        //jdbc템플릿이나 마이바티스 사용해서 쿼리 날려야됨
        int resultCount = memberRepository.bulkAgePlus(20);

        //변경되지 않은 내용이 데이터베이스에 반영, 위에 save() 메서드 끝나도 플러시하긴 함
//        em.flush();
        //영컨을 아예 비움, 스프링 데이터 JPA 는 claer() 직접 안날리고 @Modifying 메서드 옵션 사용 가능
        em.clear();

        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);

        //결과 40살로 나옴, 벌크 연산 이후에는 영속성 컨텍스트를 다 날려야됨
        //값을 save() 메서드로 저장하면 영컨에도 값이 넣어지고 데이터베이스에도 넣어짐
        //벌크성 수정 쿼리로 수정하면 영컨을 건드리지 않고 데이터베이스 값을 바로 수정하므로
        //영속성 컨텍스트의 값은 수정되지 않음
        //조회할 때는 영컨에 값 있으면 영컨에서 조회하고 영컨에 값 없으면 그 때 데이터베이스에서 조회하므로
        //save() 메서드 실행할 때 저장되었던 영속성 컨텍스트의 값이 조회됨

        //em.flush() em.clear()하면 41 나옴
        //영컨에 값 없으면 db에서 조회하므로
        System.out.println("member5 의 나이= " + member5.getAge());

        //then
//        assertThat(resultCount).isEqualTo(3);
    }

    /**
     * @EntityGraph로 패치조인 편하게 해결,
     * 패치조인을 하면 jpql써야하는 번거로움을 해결하고 메서드 이름으로 조인문을 만드므로
     * 연관된 엔티티들을 SQL 한번에 조회하는 방법
     * member team은 지연로딩 관계이다. 따라서 다음과 같이 team의 데이터를 조회할 때 마다 쿼리가 실행된다.
     * 쿼리를 날릴때 Team에 대한 쿼리를 같이 하지 않고 Team에 대한 정보를 가져와야 할때 그때 쿼리 또 날리므로(N+1 문제 발생)
     * jpa에서는 패치조인으로 이걸 해결, 정확하게 조인해서 select로 한 번에 다 갖고옴, 가짜 프록시가 아닌
     * 패치조인은 연관관게가 있는걸 그냥 조인하는게 아니라 select에 다 넣어주는
     * */
    @Test
    public void findMemberLazy() throws Exception {
        //given
        //member1 -> teamA
        //member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 10, teamA));
        memberRepository.save(new Member("member3", 20, teamB));

        em.flush();
        em.clear();

        //when
        //페치 조인과 엔티티그래프 적용하지 않고 조회
//        List<Member> members = memberRepository.findAll();

        //NamedEntityGraph
        List<Member> members = memberRepository.findMemberNamedEntityGraph();

        //then
        for (Member member : members) {
            //지연 로딩으로 조회했다면 프록시
            System.out.println("회원과 같이 갖고온 팀 객체 = " + member.getTeam().getClass());
            //페치 조인이나 엔티티그래프 적용하지 않고 조회하면 이 부분에서 추가 쿼리가 실행된다
            member.getTeam().getName();
        }
    }

    //조회용으로만 쓰는 JPA표준은 제공을 안함
    @Test
    public void queryHint() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        //when
        Member member = memberRepository.findReadOnlyByUsername("member1");

        //값을 변경하면 변경감지라고 바로 db에 업데이트 쿼리나감
        //단점, 원본이 있어야됨, 값을 2개 갖고있어야됨. 메모리를 더 먹음
        //org.hibernate.readOnly value = true하면 최적화를 해버려서 스냅샷을 안만들어
        //변경이 안된다고 가정해서 무시를한다, 변경감지 체크를 안함
        //진짜 중요하고 트래픽이 많은 API에만 넣음, 다 넣지는 않는다, 성능테스트 해보고 결정
        member.setUsername("member2");
        em.flush(); //Update Query 실행X
    }

    @Test
    public void Lock() {
        memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        //select한 다음에 for update 붙음, 방언에 따라 동작방식이 달라짐
        List<Member> result = memberRepository.findLockByUsername("member1");
    }

    //사용자 정의 리포지토리의 메서드 실행
    @Test
    public void callCuston() {
        List<Member> result = memberRepository.findMemberCustom();
    }

    //명세 테스트
    @Test
    public void specBasic() throws Exception {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);
        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);
        em.flush();
        em.clear();

        //when
        Specification<Member> spec =
                MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
        //Member에  Specification 상속받아야 spec을 파라미터로 넣을수있음
        List<Member> result = memberRepository.findAll();

        //then
        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    //동적으로 쓸때

    //Probe: 필드에 데이터가 있는 실제 도메인 객체
    //ExampleMatcher: 특정 필드를 일치시키는 상세한 정보 제공, 재사용 가능
    //Example: Probe와 ExampleMatcher로 구성, 쿼리를 생성하는데 사용

    //장점
    //동적 쿼리를 편리하게 처리
    //도메인 객체를 그대로 사용
    //데이터 저장소를 RDB에서 NOSQL로 변경해도 코드 변경이 없게 추상화 되어 있음
    //스프링 데이터 JPA JpaRepository 인터페이스에 이미 포함

    //단점
    //조인은 가능하지만 내부 조인(INNER JOIN)만 가능함 외부 조인(LEFT JOIN) 안됨
    //다음과 같은 중첩 제약조건 안됨
    //firstname = ?0 or (firstname = ?1 and lastname = ?2)
    //매칭 조건이 매우 단순함
    //문자는 starts/contains/ends/regex
    //다른 속성은 정확한 매칭( = )만 지원

    //정리
    //실무에서 사용하기에는 매칭 조건이 너무 단순하고, LEFT 조인이 안됨
    //실무에서는 QueryDSL을 사용하자
    @Test
    public void QueryByExampleTest() throws Exception {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);
        em.persist(new Member("m1", 0, teamA));
        em.persist(new Member("m2", 0, teamA));
        em.flush();

        //when
        //Probe 생성
        //엔티티 자체가 검색조건
        Member member = new Member("m1");
        Team team = new Team("teamA"); //내부조인으로 teamA 가능
        member.setTeam(team);
        //ExampleMatcher 생성, age 프로퍼티는 무시
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("age");

        Example<Member> example = Example.of(member, matcher);
        List<Member> result = memberRepository.findAll(example);

        //then
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void projections() throws Exception {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);

        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //m1만 뽑고 싶다
        //select m.username from member m
        //        where m.username=‘m1’
        //when
        List<UsernameOnly> result =
                memberRepository.findProjectionsByUsername("m1");
        //then
        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void nativeQuery() {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);

        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(1,10));
        List<MemberProjection> content = result.getContent();
        for (MemberProjection memberProjection : content) {
            System.out.println("memberProjection.getUsername() = " + memberProjection.getUsername());
            System.out.println("memberProjection.getUsername() = " + memberProjection.getTeamName());
        }
    }
}


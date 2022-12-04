package study.datajpa.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

/**
 * 명세는 잘 안씀, 중요하지 않다
 * 자바코드로 짤 수 있다는 명세라는 개념은 좋지만 구현기술이 크리테리아인게 별로
 * */
public class MemberSpec {

    //팀이름을 검색 조건으로 넣는
    public static Specification<Member> teamName(final String teamName) {
        //root는 처음 집은 엔티티
        return (Specification<Member>) (root, query, builder) -> {
            if (StringUtils.isEmpty(teamName)) {
                return null;
            }
            //크리테리아 문법
            Join<Member, Team> t = root.join("team", JoinType.INNER); //회원과조인

            //조인을 해서 결과가 파라미터로 넘어온 것과 같은지
            return builder.equal(t.get("name"), teamName);
        };
    }

    public static Specification<Member> username(final String username) {

        return (Specification<Member>) (root, query, builder) ->
                builder.equal(root.get("username"), username);
    }
}
//Specification 을 구현하면 명세들을 조립할 수 있음. where() , and() , or() , not() 제공
//findAll 을 보면 회원 이름 명세( username )와 팀 이름 명세( teamName )를 and 로 조합해서 검색조건으로 사용
//명세를 정의하려면 Specification 인터페이스를 구현
//명세를 정의할 때는 toPredicate(...) 메서드만 구현하면 되는데 JPA Criteria의 Root ,
//CriteriaQuery , CriteriaBuilder 클래스를 파라미터 제공
//예제에서는 편의상 람다를 사용
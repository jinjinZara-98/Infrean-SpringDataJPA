package study.datajpa.repository;

/**
 * Projections
 * 중첩 구조 처리
 * 회원뿐만 아니라 연관된 팀까지
 * 제네릭에 인터페이스 넣어주면 됨 */
public interface NestedClosedProjection {

    /** 회원이름, 첫번째는 최적화 되서 진짜 회원이름만 가져옴 */
    String getUsername();

    /** 두 번쨰는 최적화 안되서 팀은 엔티티를 불러옴 */
    TeamInfo getTeam();

    /** 팀에 대한 정보, 이름만
    //두번째는 최적화 안돼서 엔티티로 불러서 다 가져옴 */
    interface TeamInfo {
        String getName();
    }
}
//select
// m.username as col_0_0_,
// t.teamid as col_1_0_,
// t.teamid as teamid1_2_,
// t.name as name2_2_
//from
// member m
//left outer join
// team t
// on m.teamid=t.teamid
//where
// m.username=?

/** 주의
//프로젝션 대상이 root 엔티티면, JPQL SELECT 절 최적화 가능
//프로젝션 대상이 ROOT가 아니면
//LEFT OUTER JOIN 처리
//모든 필드를 SELECT해서 엔티티로 조회한 다음에 계산
//정리
//프로젝션 대상이 root 엔티티면 유용하다.
//프로젝션 대상이 root 엔티티를 넘어가면 JPQL SELECT 최적화가 안된다!
//실무의 복잡한 쿼리를 해결하기에는 한계가 있다.
//실무에서는 단순할 때만 사용하고, 조금만 복잡해지면 QueryDSL을 사용하자 */
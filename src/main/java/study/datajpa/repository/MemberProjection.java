package study.datajpa.repository;

//네이티브쿼리
public interface MemberProjection {

    Long getId();
    String getUsername();
    String getTeamName();
}

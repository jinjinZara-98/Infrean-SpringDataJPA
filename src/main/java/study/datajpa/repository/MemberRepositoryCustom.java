package study.datajpa.repository;

import study.datajpa.entity.Member;

import java.util.List;

/**
 * 사용자 정의 리포지토리, 실무에서 중요
 *
 * 스프링 데이터 JPA는 인터페이스므로 구현하면 모든 기능들 다 구현해야함
 * jdbc나 마이바티스 다른 리포지토리 기능을 사용할 수 있게 열어놓음, querydsl 사용할때 많이 사용
 *
 * 스프링 데이터 JPA가 아닌 직접 구현한 기능 쓰고 싶을때
 * */
public interface MemberRepositoryCustom {
    List<Member> findMemberCustom();
}

package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Web 확장 - 도메인 클래스 컨버터
 * HTTP 파라미터로 넘어온 엔티티의 아이디로 엔티티 객체를 찾아서 바인딩
 * */
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    /** 도메인 클래스 컨버터 사용 전 */
    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        //id값을 가지고 객체를 찾음
        Member member = memberRepository.findById(id).get();

        //@RestController이므로 화면에 띄우기
        return member.getUsername();
    }

    /**
     * 도메인 클래스 컨버터 사용 후, 조회용으로만 사용하자
     *
     * 어차피 id는 기본키값이므로 도메인 클래스 컨버터 사용 가능
     * 스프링이 중간에서 컨버팅하는 과정 끝내고 member에 파라미터 결과 자동의존주입해줌
     * 쿼리도 똑같이 날아감, 간단할때만 사용
     * HTTP 요청은 회원 id 를 받지만 도메인 클래스 컨버터가 중간에 동작해서 회원 엔티티 객체를 반환
     * 도메인 클래스 컨버터도 리파지토리를 사용해서 엔티티를 찾음
     * 메인 클래스 컨버터로 엔티티를 파라미터로 받으면, 이 엔티티는 단순 조회용으로만 사용해야 한다
     * */
    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) {

        return member.getUsername();
    }

    /**
     * Web 확장 - 페이징과 정렬
     *
     * 스프링 데이터가 제공하는 페이징과 정렬 기능을 스프링 MVC에서 편리하게 사용할 수 있다.
     * Pageable은 인터페이스 파라미터 정보, Page는 인터페이스 결과 정보
     * http파라미터들이 컨트롤러에서 바인딩 될 때 pageble이 있으면 pagerequest란 객체를 생성해서 여기다 주입해줌
     * url에 ?page=0하면 데이터를 20개까지만 꺼냄, 1하면 다음 20개
     * &size = 하면 그 페이지에서 지정해준 개수만큼 불러옴
     * &sort=id, desc id를 역순으로 가져옴
     * 예) /members?page=0&size=3&sort=id,desc&sort=username,desc
     * page: 현재 페이지, 0부터 시작한다.
     * size: 한 페이지에 노출할 데이터 건수
     * sort: 정렬 조건을 정의한다. 예) 정렬 속성,정렬 속성...(ASC | DESC), 정렬 방향을 변경하고 싶으면 sort
     * 파라미터 추가 ( asc 생략 가능)
     *
     * 반환타입이 page이므로 totalcountquery가 결과로 나감
     * */
    @GetMapping("/members")
    /**
     * @PageableDefault는 개별설정, 글로벌설정(yml파일)보다 우선순위 높음
     * */
    public Page<Member> list(@PageableDefault(size = 5) Pageable pageable) {
        //findAll의 PagingAndSortingRepository로 pageable넘겨주기만 하면됨

        Page<Member> page = memberRepository.findAll(pageable);
        //엔티티를 노출하지말고 dto로 변환해야함
//        Page<MemberDto> pageDto = page.map(member -> new MemberDto(member.getId(), member.getUsername(),null ));
        Page<MemberDto> pageDto = page.map(member -> new MemberDto(member));
        //Page<MemberDto> pageDto = page.map(MemberDto::new) 이렇게 줄일 수 있음
        return page;
    }

    /**
     * 스프링이 어플리케이션 올라올때 이게 한 번 실행되는
     * 값이 없으니 데이터 넣는 코드
     * */
//    @PostConstruct
    public void init() {

        //id 자동으로 생성되니
        for(int i=0; i<100; i++) {
            memberRepository.save(new Member("user" + i, i));
        }
    }

}
/**
 * 접두사
 * 페이징 정보가 둘 이상이면 접두사로 구분, 페이징 하는게 2개
 * @Qualifier 에 접두사명 추가 "{접두사명}_xxx”
 * 예제: /members ? member_page=0 & order_page=1
 * public String list(
 *  @Qualifier("member") Pageable memberPageable,
 *  @Qualifier("order") Pageable orderPageable, ...
 *
 * Page를 1부터 시작하기
 * 스프링 데이터는 Page를 0부터 시작한다.
 * 1. Pageable, Page를 파리미터와 응답 값으로 사용히지 않고, 직접 클래스를 만들어서 처리한다. 그리고
 * 직접 PageRequest(Pageable 구현체)를 생성해서 리포지토리에 넘긴다.
 *  물론 응답값도 Page 대신에 직접 만들어서 제공해야 한다
*/
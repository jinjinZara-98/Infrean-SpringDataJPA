package study.datajpa.entity;


import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter
//생성자, protected까지만 허용됨
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//객체 찍을때 출력할 내용, 연관관계 필드는 안넣는게 좋음
@ToString(of = {"id", "username", "age"})
//쿼리에 이름을 부여하고 호출하는 기능, 재활용하는, 이 쿼리를 다시 사용할때 이 이름만 불러, 실무에서 잘 안 쓴다?
//메서드 이름으로 쿼리를 만드는것과 비교해봤을때 장점은 쿼리문이 틀리면 컴파일오류로 알수있음
@NamedQuery(
        name="Member.findByUsername",
        query="select m from Member m where m.username = :username")

//@NamedQuery처럼 엔티티그래프에 이름 지정하고 재활용하기 좋게
//@NamedEntityGraph(name = "Member.all", attributeNodes = {@NamedAttributeNode("team"), @NamedAttributeNode("team")})
@NamedEntityGraph(name = "Member.all", attributeNodes = @NamedAttributeNode("team"))
public class Member extends BaseEntity {

    @Id
    @GeneratedValue
    //db테이블은 이 키값으로 매핑
    @Column(name = "member_id")
    private Long id;

    private String username;

    private int age;

    //다대일 관계, 무조건 지연로딩
    @ManyToOne(fetch = FetchType.LAZY)
    //외래키 명
    @JoinColumn(name = "team_id")
    private Team team;

    //이름만 받는 생성자
    public Member(String username) {
        this(username, 0);
    }

    //이름과 나이를 받는 생성자
    public Member(String username, int age) {

        this(username, age, null);
    }

    //이름과 나이 소속 팀을 받는 생성자
    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;

        //파라미터로 들어온 팀이 비어있지 않다면 팀 변경 메서드 실행
        if (team != null) {
            changeTeam(team);
        }
    }

    //양방향이면 서로 값 세팅, 한 코드로 해결
    //==연관관계 메서드==//
    //팀 변경 메서드, 파라미터로 들어온 새로운팀에 현재 Member객체 넣기
    public void changeTeam(Team team) {
        this.team = team;//현재 회원의 팀에 파라미터 팀을 세팅
        team.getMembers().add(this);//파라미터로 들어온 팀의 회원리스트를 갖고와 현재 생성하는 회원 객체를 추가가
    }
}
package study.datajpa.dto;

import lombok.Data;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

/** Data는 엔티티에는 쓰면 안된다? */
@Data
public class MemberDto {

    /** 조회하고 싶은 대상 */
    private Long id;
    private String username;
    private String teamName;

    public MemberDto(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }

    /** dto는 엔티티를 봐도 괜찮음 */
    public MemberDto(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
    }
}
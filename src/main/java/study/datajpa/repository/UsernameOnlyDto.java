package study.datajpa.repository;

/**
 * 프로젝션
 * 다음과 같이 인터페이스가 아닌 구체적인 DTO 형식도 가능
 * 프록시가 필요가 없음, 구체적인 클래스를 명시했기 때문에
 * 이 클래스의 객체 생성자에 반환을 해줌
 * */
public class UsernameOnlyDto {

    private final String username;

    //생성자의 파라미터 이름으로 매칭, 파라미터 이름을 분석함
    public UsernameOnlyDto(String username) {
        this.username = username;
    }
    public String getUsername() {
        return username;
    }
}

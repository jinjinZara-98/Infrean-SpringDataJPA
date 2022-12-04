package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.*;
import java.util.Optional;
import java.util.UUID;

/**
 * Auditing
 * 스프링 데이터 JPA 사용, 생성시간 수정시간 알기 위해
 *
 */
@EnableJpaAuditing
/**
 * 공통 인터페이스 설정, 스프링 부트 사용시 생략 가능
 * 스프링 부트 사용시 @SpringBootApplication 위치를 지정(해당 패키지와 하위 패키지 인식)
 * 여기 위치부터 시작해 하위패키지들 자동으로 끌어올 수 있음
 * 만약 위치가 달라지면 @EnableJpaRepositories 필요
 */
//@EnableJpaRepositories(basePackages = "jpabook.jpashop.repository")
@SpringBootApplication
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}

	/**
	 * BaseEntity의 등록자 수정자
	 * AuditorAware의 getCurrentAuditor를 구현
	 */
	@Bean
	public AuditorAware<String> auditorProvider() {
		//세션정보를 꺼내거나, httpsession에서 어떻게든 이걸 꺼내거나 해서 userid를 넣어
		//등록되거나 수정이 될때 이 메서드를 호출해서 결과물을 꺼내가 등록자 수정자의 값이 채워짐
		return () -> Optional.of(UUID.randomUUID().toString());
	}
}

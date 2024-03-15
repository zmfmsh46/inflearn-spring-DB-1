package hello.jdbc.repository;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.connection.ConnectionConst;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class MemberRepositoryV1Test {

    MemberRepositoryV1 repository;

    //각 테스트가 실행되기 직전에 실행
    @BeforeEach
    void beforeEach() {
        //기본 DriverManager - 항상 새로운 커넥션을 흭득
        //DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

        //커넥션 풀링
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        repository = new MemberRepositoryV1(dataSource);
    }

    @Test
    void crud() throws SQLException, InterruptedException {
        //save
        Member member = new Member("memberV3", 10000);
        repository.save(member);

        //findById
        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember={}", findMember);
        //서로 다른 객체이지만
        //Member의 @Data 는 해당 객체의 모든 필드를 사용하도록 equals() 를 오버라이딩 하기 때문에
        //equals()를 통해 값만 같다면 true를 반환한다.
        log.info("member == findMember {}", member == findMember);
        log.info("member equals findMember {}", member.equals(findMember));
        assertThat(findMember).isEqualTo(member);

        //update [money: 10000 -> 20000]
        repository.update(member.getMemberId(), 20000);
        Member updatedMember = repository.findById(member.getMemberId());
        assertThat(updatedMember.getMoney()).isEqualTo(20000);

        //delete
        repository.delete(member.getMemberId());
        //삭제된 멤버 조회 시 발생하는 예외를 받아 성공적으로 삭제됬음을 알 수 있다.
        assertThatThrownBy(() -> repository.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);

        Thread.sleep(1000);
    }
}
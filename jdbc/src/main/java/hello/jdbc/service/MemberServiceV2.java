package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 파라미터 연동, 커넥션 풀을 고려한 종료
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    //fromId 의 회원을 조회해서 toId 의 회원에게 money만큼의 돈을 계좌이체하는 로직
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        Connection con = dataSource.getConnection();
        try {
            //트랜잭션 시작
            con.setAutoCommit(false);
            //비즈니스 로직
            bizLogic(con, fromId, toId, money);
            //트랜잭션 종료 : 성공 시 커밋
            con.commit();
        } catch (Exception e) {
            //트랜잭션 종료 : 실패 시 롤백
            con.rollback();
            throw new IllegalStateException(e);
        } finally {
            //풀로 커넥션 반환
            release(con);
        }

    }
    private void bizLogic(Connection con, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(con, fromId);
        Member toMember = memberRepository.findById(con, toId);

        memberRepository.update(con, fromId, fromMember.getMoney() - money);
        //예외상황 테스트를 위한 예외
        validation(toMember);
        memberRepository.update(con, toId, toMember.getMoney() + money);
    }
    private static void release(Connection con) {
        if (con != null) {
            try {
                //오토커밋 모드를 되돌려놓고 반환해줘야함.
                con.setAutoCommit(true);
                con.close();
            } catch (Exception e) {
                log.info("error", e);
            } 
        }
    }
    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("memberEX")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}

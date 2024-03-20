package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;

/**
 * SQLExceptionTranslator 추가
 */
@Slf4j
public class MemberRepositoryV5 implements MemberRepository{
    /*
        JdbcTemplate 은 JDBC로 개발할 때 발생하는 반복을 대부분 해결해준다.
        그 뿐만 아니라 지금까지 학습했던, 트랜잭션을 위한 커넥션 동기화는 물론이고,
        예외 발생시 스프링 예외 변환기도 자동으로 실행해준다.
     */
    private final JdbcTemplate template;
    public MemberRepositoryV5(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }
    @Override
    public Member save(Member member) {
        String sql = "insert into member(member_id, money) values (?, ?)";
        template.update(sql, member.getMemberId(), member.getMoney());
        return member;
    }
    @Override
    public Member findById(String memberId) {
        String sql = "select * from member where member_id = ?";
        //한 건 조회 : queryForObject
        return template.queryForObject(sql, memberRowMapper(), memberId);
    }
    private RowMapper<Member> memberRowMapper() {
        return (rs, rowNum) -> {
            Member member = new Member();
            member.setMemberId(rs.getString("member_id"));
            member.setMoney(rs.getInt("money"));
            return member;
        };
    }
    @Override
    public void update(String memberId, int money) {
        String sql = "update member set money=? where member_id=?";
        template.update(sql, money, memberId);
    }
    @Override
    public void delete(String memberId) {
        String sql = "delete from member where member_id=?";
        template.update(sql, memberId);
    }
}

package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * 예외 누수 문제 해결
 * SQLException 제거
 *
 * MemberRepository 인터페이스 의존
 */
@Slf4j
public class MemberServiceV4 {
    private final MemberRepository memberRepository;

    public MemberServiceV4(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /*
        이 메서드가 실행될 때 트랜잭션 적용. (성공하면 커밋, 실패하면 롤백)
        순수한 비즈니스 로직만 남기고 트랜잭션 관련 코드는 모두 제거함.
        @Transactional 애노테이션은 메서드에 붙여도 되고, 클래스에 붙여도 된다.
        클래스에 붙이면 외부에서 호출 가능한 public 메서드가 AOP 적용 대상이 된다.
    */
    @Transactional
    public void accountTransfer(String fromId, String toId, int money) {
        //비즈니스 로직
        bizLogic(fromId, toId, money);
    }

    private void bizLogic(String fromId, String toId, int money) {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        //예외상황 테스트를 위한 예외
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }
    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("memberEX")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}

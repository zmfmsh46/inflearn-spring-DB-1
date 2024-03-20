package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@Slf4j
public class CheckedTest {

    @Test
    void checked_catch() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void checkedThrow() {
        Service service = new Service();
        assertThatThrownBy(() -> service.callThrow())
                .isInstanceOf(MyCheckedException.class);
    }

    /**
     * 컴파일러가 체크하는 checkedException
     * Exception을 상속받는 예외는 체크 예외가 된다.
     */
    static class MyCheckedException extends Exception {
        public MyCheckedException(String message) {
            super(message);
        }
    }

    static class Service {
        Repository repository = new Repository();
        //던진 예외를 잡아서 처리하는 코드
        public void callCatch() {
            try {
                repository.call();
            } catch (MyCheckedException e) {
                log.info("예외 처리, message={}", e.getMessage(), e);
            }
        }
        //던진 예외를 또 던지는 코드
        public void callThrow() throws MyCheckedException {
            repository.call();
        }
    }

    static class Repository {
        //체크예외는 컴파일러가 체크를 해주기 때문에 무조건 잡거나(catch) 던져야(throws) 한다.
        public void call() throws MyCheckedException {
            throw new MyCheckedException("ex");
        }
    }
}

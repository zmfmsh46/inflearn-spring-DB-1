package hello.jdbc.exception.basic;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class UncheckedAppTest {
    @Test
    void unchecked() {
        Controller controller = new Controller();
        assertThatThrownBy(() -> controller.request())
                .isInstanceOf(RuntimeSQLException.class);
    }

    @Test
    void printEx() {
        Controller controller = new Controller();
        try {
            controller.request();
        } catch (Exception e) {
            //e.printStacktTrace()
            log.info("ex", e);
        }
    }

    static class Controller {
        Service service = new Service();
        public void request() {
            service.logic();
        }
    }
    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();
        public void logic() {
            repository.call();
            networkClient.call();
        }
    }
    static class NetworkClient {
        public void call() {
            throw new RuntimeConnectException("연결 실패");
        }
    }
    static class Repository {
        public void call() {
            try {
                runSQL();
            } catch (SQLException e) {
                //체크예외를 잡아 언체크 예외(런타임 예외)로 바꿔 던짐
                //예외를 전환할 때는 꼭! 기존 예외를 포함해야 한다.
                // 그렇지 않으면 스택 트레이스를 확인할 때 심각한 문제가 발생한다
                throw new RuntimeSQLException(e);
            }
        }
        public void runSQL() throws SQLException {
            throw new SQLException("ex");
        }
    }
    static class RuntimeConnectException extends RuntimeException{
        public RuntimeConnectException(String message) {
            super(message);
        }
    }
    static class RuntimeSQLException extends RuntimeException {
        //기존 예외(e)를 포함하지 않아서 기존에 발생한 java.sql.SQLException 과 스택 트레이스를 확인할 수 없다.
        public RuntimeSQLException() {

        }
        //기존 예외(e)를 포함해서 기존에 발생한 java.sql.SQLException 과 스택 트레이스를 확인할 수 있다.
        public RuntimeSQLException(Throwable cause) {
            super(cause);
        }
    }
}

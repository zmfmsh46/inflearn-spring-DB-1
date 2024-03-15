package hello.jdbc.connection;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class DBConnectionUtil {
    public static Connection getConnection() {
        try {
            //JDBC 표준 인터페이스를 사용.
            //JDBC가 제공하는 DriverManager가 라이브러리에 등록된 H2 드라이버에서 제공하는
            //JDBC 표준 인터페이스를 구현한 H2 전용 커넥션을 찾아 실행.
            //DB가 바뀌어도 수정할 필요없다.
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            log.info("get connection={}, class={}", connection, connection.getClass());
            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}

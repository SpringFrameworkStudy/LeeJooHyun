import java.sql.Connection;
import java.sql.SQLException;

public class DConnectionMaker implements ConnectionMaker{
    @Override
    public Connection makeConnection() throws ClassNotFoundException, SQLException {
        // D 사가 족자적인 방법으로 Connection 을 생성하는 코드
        return null;
    }
}

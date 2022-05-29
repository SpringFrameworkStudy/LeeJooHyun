import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SelectStatement implements StatementStrategy {
    @Override
    public PreparedStatement makePreparedStatement(Connection c) throws SQLException {

        return null;
    }
}

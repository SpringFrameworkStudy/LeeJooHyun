import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    private ConnectionMaker connectionMaker;
    private Connection connection;
    private User user;

    public UserDao(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }

    public void add(User user) throws ClassNotFoundException, SQLException    {
        this.connection = connectionMaker.makeConnection();

    }

    public User get(String id) throws ClassNotFoundException, SQLException{
        this.connection = connectionMaker.makeConnection();
        this.user = new User();
//        this.user.setId(re.getString("id"));
        return user;
    }
}

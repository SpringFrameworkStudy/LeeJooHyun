

import java.sql.Connection;
import java.sql.SQLException;

public class UserDao {

    private ConnectionMaker connectionMaker;
    private Connection connection;
    private User user;

    public UserDao() {
    }

    public ConnectionMaker getConnectionMaker() {
        return connectionMaker;
    }

    public void setConnectionMaker(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

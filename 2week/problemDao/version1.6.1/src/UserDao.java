import java.sql.Connection;

public class UserDao {
    private static UserDao INSTANCE;
    private ConnectionMaker connectionMaker;

    private UserDao(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }

    public static synchronized UserDao getInstance(){
//        if(INSTANCE == null) INSTANCE = new UserDao();
        return INSTANCE;
    }
}

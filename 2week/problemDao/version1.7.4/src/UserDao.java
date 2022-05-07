import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.Connection;
import java.sql.SQLException;

public class UserDao {

    private ConnectionMaker connectionMaker;
    private Connection connection;
    private User user;

    public UserDao() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        this.connectionMaker = context.getBean("connectionMaker", ConnectionMaker.class);
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

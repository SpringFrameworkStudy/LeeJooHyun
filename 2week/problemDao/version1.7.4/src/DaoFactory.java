import org.springframework.context.annotation.Configuration;

@Configuration
public class DaoFactory {
//    @Bean
    public UserDao userDao(){
        return new UserDao();
    }

//    @Bean
    public ConnectionMaker connectionMaker(){
        return new DConnectionMaker();
    }
}

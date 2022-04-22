import java.sql.Connection;
import java.sql.SQLException;

public class Test {

    static class NUserDao extends UserDao {
        @Override
        public Connection getConnection() throws ClassNotFoundException, SQLException {
            // N사의 DB Connection 생성 코드
            return null;
        }
    }

    static class DUserDao extends UserDao {
        @Override
        public Connection getConnection() throws ClassNotFoundException, SQLException {
            // D사의 DB Connection 생성 코드
            return null;
        }
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
//        UserDao dao = new DUserDao();
        UserDao dao = new NUserDao();

        User user = new User();
        user.setId("아이디12");
        user.setName("이름");
        user.setPassword("비밀번호");

        dao.add(user);

        System.out.println(user.getId() + " 등록 성공");

        User user2 = dao.get(user.getId());
        System.out.println(user2.getName());

        System.out.println(user2.getPassword());

        System.out.println(user2.getId() + "조회 성공");
    }
}

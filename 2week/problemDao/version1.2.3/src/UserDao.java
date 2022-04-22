import java.sql.*;

public abstract class UserDao {

    public void add(User user) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        Connection c = getConnection();
        PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values (?,?,?)");
        ps.setString(1, user.getId());
        ps.setString(2, user.getName());
        ps.setString(3, user.getPassword());
        ps.executeUpdate();

        ps.close();
        c.close();
    }

    public User get(String id) throws ClassNotFoundException, SQLException{
        Connection c = getConnection();
        PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();
        rs.next();
        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("Name"));
        user.setPassword(rs.getString("password"));

        rs.close();
        ps.close();
        c.close();

        return user;
    }

    /**
     * 상속을 통한 확장방법을 제공
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public abstract Connection getConnection() throws ClassNotFoundException, SQLException;
}

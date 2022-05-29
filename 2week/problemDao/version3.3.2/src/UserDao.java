import org.springframework.dao.EmptyResultDataAccessException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class UserDao {

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    abstract protected PreparedStatement makeStatement(Connection c) throws SQLException;

    public void jdbcContextWithStatementStrategy(StatementStrategy stmt) throws SQLException{
        Connection c = null;
        PreparedStatement ps = null;

        try{
            c=dataSource.getConnection();

            // 전략이 담길 그릇?
            ps = stmt.makePreparedStatement(c);

            ps.executeUpdate();
        }catch (SQLException e){
            throw e;
        }finally {
            if(ps != null){ try{ ps.close(); }catch (SQLException e){} }
            if (c!= null){ try{ c.close(); }catch (SQLException e){} }
        }
    }


    public void add(final User user) throws SQLException {
        jdbcContextWithStatementStrategy(
                new StatementStrategy() {
                    @Override
                    public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                        PreparedStatement ps = c.prepareStatement("insert into users() values(?,?,?)");
                        ps.setString(1, user.getId());
                        ps.setString(2, user.getName());
                        ps.setString(3, user.getPassword());
                        return ps;
                    }
                }
        );
    }

    public User get(String id) throws ClassNotFoundException, SQLException{
        Connection c = dataSource.getConnection();
        PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();

        User user = null;
        if(rs.next()){
            user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("Name"));
            user.setPassword(rs.getString("password"));
        }

        rs.close();
        ps.close();
        c.close();

        if(user == null) throw new EmptyResultDataAccessException(1);
        return user;
    }

    public void deleteAll() throws SQLException{
        DeleteAllStatement st = new DeleteAllStatement();
        jdbcContextWithStatementStrategy(st);
    }

    public int getCount() throws SQLException{

        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            c = dataSource.getConnection();
            ps = c.prepareStatement("select count(*) from users");
            rs = ps.executeQuery();
            rs.next();
            return  rs.getInt(1);
        } catch (SQLException e){
            throw e;
        }finally {
            if(rs!=null){
                try{
                    rs.close();
                }catch (SQLException e){

                }
            }
            if(ps != null){
                try{
                    ps.close();
                }catch (SQLException e){

                }
            }
            if (c!= null){
                try{
                    c.close();
                }catch (SQLException e){

                }
            }
        }
    }
}

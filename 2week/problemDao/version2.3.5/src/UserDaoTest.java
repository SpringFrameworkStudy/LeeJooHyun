import org.junit.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.is;


public class UserDaoTest {
    private UserDao dao;
    private User user1;
    private User user2;
    private User user3;

    @Before
    public void setup(){
        ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
        this.dao = context.getBean("userDao", UserDao.class);

        this.user1 = new User("아이디133", "이름1", "비밀번호1");
        this.user2 = new User("leegw700", "이름1", "spring2");
        this.user3 = new User("bunjin", "이름이다.", "spring203");

        System.out.println(context);
        System.out.println(this);
    }

    @Test
    public void addAndGet() throws SQLException, ClassNotFoundException {
        dao.deleteAll();
        Assert.assertThat(dao.getCount(), is(0));

        dao.add(user1);
        dao.add(user2);

        User userGet1 = dao.get(user1.getId());
        Assert.assertThat(userGet1.getName(), is(user1.getName()));
        Assert.assertThat(userGet1.getPassword(), is(user1.getPassword()));

        User userGet2 = dao.get(user2.getId());
        Assert.assertThat(userGet2.getName(), is(user2.getName()));
        Assert.assertThat(userGet2.getPassword(), is(user2.getPassword()));
    }

    @Test
    public void count() throws SQLException{
        dao.deleteAll();
        Assert.assertThat(dao.getCount(), is(0));

        dao.add(user1);
        Assert.assertThat(dao.getCount(), is(1));
        dao.add(user2);
        Assert.assertThat(dao.getCount(), is(2));
        dao.add(user3);
        Assert.assertThat(dao.getCount(), is(3));
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void getUserFailure() throws SQLException, ClassNotFoundException {
        dao.deleteAll();
        Assert.assertThat(dao.getCount(), is(0));
        dao.get("unknown_id");
    }
}

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")
public class UserDaoTest {
    @Autowired private ApplicationContext context;

    private UserDao dao;
    private User user1;
    private User user2;
    private User user3;

    @Before
    public void setup(){
        this.dao = context.getBean("userDao", UserDao.class);
        this.user1 = new User("아이디133", "이름1", "비밀번호1");
        this.user2 = new User("leegw700", "이름1", "spring2");
        this.user3 = new User("bunjin", "이름이다.", "spring203");

        System.out.println(this.context);
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

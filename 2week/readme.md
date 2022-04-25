# 1.1 초난감한 DAO(난감하지 않게 해주기 위한 여정)
User.java 
```java
public class User {
    String id;
    String name;
    String password;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
```
---
UserDao.java
```java
public class UserDao {

    public void add(User user) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3307/problem", "root", "1234");
        PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values (?,?,?)");
        ps.setString(1, user.getId());
        ps.setString(2, user.getName());
        ps.setString(3, user.getPassword());
        ps.executeUpdate();

        ps.close();
        c.close();
    }

    public User get(String id) throws ClassNotFoundException, SQLException{
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3307/problem", "root", "1234");
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
}
```
---
Main.java
```java 
public class Test {
    public static void main(String[] args) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UserDao dao = new UserDao();

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

```
---
# 1.2 DAO의 분리

#### 1.2.1 관심사를 알아내서 분리 시킴(변화에 유연)
 UserDao.java

 ```java
public class UserDao {

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

    // 중복 코드의 메소드 추출 -> 관심사의 분리
    public Connection getConnection() throws ClassNotFoundException, SQLException{
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3307/problem", "root", "1234");
        return c;
    }
}

 ```
 ---
## 1.2.3 여기서 상속을 통한 확장으로 더 변화에 유연하게 분리

UserDao.java
```java
  /**
     * 상속을 통한 확장방법을 제공
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public abstract Connection getConnection() throws ClassNotFoundException, SQLException;
```
---
Main.java
```java
public class SubClass {

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

```
---
**여기서 "상속으로 기능을 확장" 하는 2가지 디자인 패턴을 알려주신다.**

**1. 템플릿 메소드 패턴 - 슈퍼 클래스에서 어떤 기능을 실행하는 로직(템플릿 메소드)에서 변화가 필요한 부분(강제적 - 추상 메소드, 선택적 - 훅 메소드)을 알잘딱으로 구현해서 사용하도록 하는 방법**
```java
public abstract class Super {
	/// 템플릿 메소드 
    public void templateMethod(){
        hookMethod();
        abstractMethod();
    }
    // 선택적으로 오버라이드 가능한 훅 메소드
    protected void hookMethod() {}

    // 서브클래스에서 반드시 구현해야되는 추상 메소드
    public abstract void abstractMethod();
}
```
---
```java
public class Sub1 extends Super{

    @Override
    protected void hookMethod() {
        super.hookMethod();
    }

    @Override
    public void abstractMethod() {

    }
}
```

**2. 팩토리 메소드 패턴 - 서브클래스에서 구체적인 오브젝트 생성 방법을 결정하게 하는 것**
UserDao.java
```java
  /**
     * 상속을 통한 확장방법을 제공
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public abstract Connection getConnection() throws ClassNotFoundException, SQLException;
```
---
```java
public class SubClass {

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
```
- 서브클래스의 getConnection()을 통해 만들어진 Connection 오브젝트의 종류가 다르거나 같다. 
	> 여기서는 Connection 인터페이스를 상속 받는 클래스들이거나 인터페이스의 객체들
- DUserDao, NUserDao의 입장에선 어떤식으로 Connection 기능을 제공할지만 관심대상이다. 
- 상 하위 클래스의 각자의 관심 사항을 분리 시키기 위해 쓴다고 할 수 있다. 


## 하지만 여기에는 한계가 있따..
1. 상속을 사용했다는 점
 	- 자바는 다중 상속을 허용하지 않는다. 
    	- extends의 상속 구조인 경우 구현체를 가진 이름이 똑같은 똑같은 추상 메소드를 만났을때 어떤 놈을 써야 될지 멘붕이 온다.
	- 커넥션 객체를 가져오려고 상속 구조를 만들어 버리면 그 이후에 다른 목적의 상속을 적용하기 힘들다. 
2. 여전히 상속을 통한 상하위 관계는 밀접하다.
3. 추상 메소드 구현체의 중복

---
# 1.3 DAO의 확장
## 1.3.1 독립적인 클래스로 분리
SimpleConnectionMaker.java
```java
public class SimpleConnectionMaker {
    public Connection makeNewConnection() throws ClassNotFoundException, SQLException{
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3307/problem", "root", "1234");
        return c;
    }
}
```
UserDao.java
```java
public class UserDao {

    private SimpleConnectionMaker simpleConnectionMaker;

    public UserDao() {
        this.simpleConnectionMaker = new SimpleConnectionMaker();
    }
    public void add(User user) throws ... {
    	Connection c = simpleConnectionMaker.makeNewConnection();
    	...
    }
    public void get(String id) throws ... {
    	Connection c = simpleConnectionMaker.makeNewConnection();
    	...
    }
}
```
- 이번엔 UserDao 코드의 수정 없이 DB 커넥션 생성 기능을 변경할 방법이 없다.
	- Connection 인터페이스를 쓰는 메소드가 천개면 다 바꿔야됨.
- SimpleConnectionMaker에 완전히 종속됨.

### 이래선 상속을 이용한 방법만도 못한거 아닌가 싶다...
---

## 1.3.2 인터페이스의 도입
- 이때 필요한게 서로 긴밀하게 연결되지 않도록 중간에 **추상적인 느슨한 연결고리** 필요

ConnectionMaker.java
```java
public interface ConnectionMaker {
    public Connection makeConnection() throws ClassNotFoundException, SQLException;
}
```
DConnectionMaker.java (여기서 커넥션을 여기서 구현)
```java
public class DConnectionMaker implements ConnectionMaker{
    @Override
    public Connection makeConnection() throws ClassNotFoundException, SQLException {
        // D 사가 족자적인 방법으로 Connection 을 생성하는 코드
        return null;
    }
}
```

```java
public class UserDao {

    private ConnectionMaker connectionMaker;

    public UserDao() {
        // 그래도 여기는 클래스 이름이 나오네?
        this.connectionMaker = new DConnectionMaker();
    }
    public void add(User user) throws ... {
    	Connection c = connectionMaker.makeConnection();
    	...
    }
    public void get(String id) throws ... {
    	Connection c = connectionMaker.makeConnection();
    	...
    }
}
```

### 그래도 생성자의 코드는 제거되지 않고 남아았다.
- 결국 UserDao의 생성자 메소드를 직접 수정해야 확장을 할 수 있다. 
---
## 1.3.3 관계설정 책임의 분리
- 여기서 토비님은 클라리언트 오브젝트에 관한 이야기를 해주신다.
>사용되는 오브젝트를 서비스, 사용하는 오브젝트를 클라이언트

- 위 코드의 생성자에서 ConnectionMaker 인터페이스 구현 클래스의 관계를 결정해주는 기능을 분리해서 두기 적당한 장소가 **UserDao를 사용하는 클라이언트 오브젝트**이다.

- 클래스 사이의 관계를 설정해주는 것이 아닌, **오브젝트와 오브젝트의 동적인 관계**를 설정해줘야 된다는 게 핵심 -> 다형성을 이용하라는 말씀이시다.

ConnectionMaker.java
```java
public interface ConnectionMaker {
    public Connection makeConnection() throws ClassNotFoundException, SQLException;
}
```
DConnectionMaker.java
```java
public class DConnectionMaker implements ConnectionMaker{
    @Override
    public Connection makeConnection() throws ClassNotFoundException, SQLException {
        // D 사가 족자적인 방법으로 Connection 을 생성하는 코드
        return null;
    }
}

```
UserDao.java
```java
public class UserDao {

    private ConnectionMaker connectionMaker;

	public UserDao(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }
    public void add(User user) throws ... {
    	Connection c = connectionMaker.makeConnection();
    	...
    }
    public void get(String id) throws ... {
    	Connection c = connectionMaker.makeConnection();
    	...
    }
}
```

```java
public class UserDaoTest {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        ConnectionMaker connectionMaker = new DConnectionMaker();
//        ConnectionMaker connectionMaker = new NConnectionMaker();
		UserDao dao = new UserDao(connectionMaker);
```
![](https://velog.velcdn.com/images/joohyun333/post/6ee55f6d-583f-4416-ac93-9a6eeb775932/image.jpg)

- ConnectionMaker의 구현체의 오브젝트 간 관계를 맺는 책임을 최종 클라이언트 UserDaoTest(UserDao를 사용하는 클라이언트)에 떠넘겨 버리면서 모든 관심사를 독립적으로 분리했다.
- 이렇게함으로써 UserDao에는 전혀 손대지 않고 모든 고객이 만족스럽게 DB연결 기능을 확장하여 사용이 가능해졌다.
---
## 1.3.4 원칙과 패턴
### 개방 패쇄 원칙(OCP, Open-Close Principle)
> 클래스와 모듈은 확장에는 열려있고, 변경에는 닫혀있다.
- UserDao는 DB연결 기능을 확장하는 데에는 열렸고, UserDao의 핵심코드는 그런 변화에 영향을 받지 않고 유지할 수 있다.

### 높은 응집도와 낮은 결합도
- 응집도 凝集度
凝 (엉길 응) 集 (모을 집) 度 (법도 도, 헤아릴 탁, 살 택)
> - 높은 응집도
변화가 일어날때 해당 모듈에서 많은 부분이 함께 바뀐다.
응집도가 높다는 건 하나의 모듈, 클래스가 하나의 책임 또는 관심사에만 집중되있다고 볼 수 있다.

- 결합도 結合度 - 하나의 오브젝트가 변할때 관계를 맺은 다른 오브젝트에게 변화를 요구하는 정도
結 (맺을 결, 상투 계) 合 (합할 합, 쪽문 합, 홉 홉) 度 (법도 도, 헤아릴 탁, 살 택)
> - 낮은 결합도
관계를 맺고 있는 다른 오브젝트에게 변경에 대한 요구가 전파되지 않음을 의미 - 느슨한 연결

### 전략 패턴(Strategy Pattern)
- 변경이 필요한 알고리즘(독립적인 책임으로 분리가 가능한 기능) 인터페이스 를 통해 분리시키고, 이를 구현한 구체 클래스를 필요에 따라 바꿔서 사용 가능한 패턴
- 여기서 전략이라 불리는 것은 구체 클래스를 전략이라고 보고 이 구체 클래스를 바꿔가면서 사용할 수 있음이라고 생각하면 된다. 
- 초난감 DAO로 치면 아래 경우다.
> ConnectionMaker connectionMaker = new DConnectionMaker() --> 전략;

### 여기서 토비님이 슬슬 스프링에 대한 이야기를 할 때가 됐다고 말하시는데... 

# 1.4 제어의 역전(IoC)
## 1.4.1 오브젝트 팩토리
- 이번엔 엉겹결에 UserDao로부터 어떤 구현 클래스를 사용할지를 결정하는 기능을 떠맡은 UserDaoTest의 관심사를 분리해서 리펙토링 해보자.

### 팩토리
- 객체의 생성을 담당하고, 만들어진 오브젝트를 돌려주는 역할을 하는 오브젝트
(추상 팩토리 패턴, 팩토리 메소드 패턴과는 다르니 혼동 ㄴㄴ)
- 단지 오브젝트를 생성하는 쪽과 오브젝트를 사용하는 쪽의 역할과 책임을 분리하려는 목적으로 사용
- 오브젝트를 구성하고 그 관계를 정의하는 책임

DaoFactory.java
```java
public class DaoFactory {
    public UserDao userDao(){
        DConnectionMaker connectionMaker = new DConnectionMaker();
        UserDao userDao = new UserDao(connectionMaker);
        return userDao;
    }
}

```
UserDaoTest.java
```java
public class UserDaoTest {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        UserDao dao = new DaoFactory().userDao();
        ...
   	}
}
```
- 이제 UserDaoTest는 UserDao에게 객체를 받아 자신의 관심사인 테스트만 하면 된다.


### 설계도로서의 팩토리
- UserDao, ConnectionMaker는 애플리케이션의 핵심, 기술 로직을 담당하는 컴포넌트
- DaoFactory는 컴포넌트의 구조와 관계를 정의한 설계도

> 두 역할의 오브젝트를 분리함

## 1.4.2 오브젝트 팩토리의 활용
- 여기서 다른 DAO의 생성 기능을 넣으면?
```java
public class DaoFactory {
    public UserDao userDao(){
        return new UserDao(new DConnectionMaker());
    }
    
    public AccountDao accountDao(){
        return new AccountDao(new DConnectionMaker());
    }

    public MessageDao messageDao(){
        return new MessageDao(new DConnectionMaker());
    }
}
```
- new DConnectionMaker()가 메소드마다 반복 -> ConnectionMaker의 구현 클래스를 바꿀 때마다 모든 메소드를 일일히 수정해야 됨.

```java
public class DaoFactory {
    public UserDao userDao(){
        return new UserDao(connectionMaker());
    }

    public AccountDao accountDao(){
        return new AccountDao(connectionMaker());
    }

    public MessageDao messageDao(){
        return new MessageDao(connectionMaker());
    }
    
    public ConnectionMaker connectionMaker(){
        return new DConnectionMaker();
    }
}
```
- ConnectionMaker의 구현 클래스를 결정하고 오브젝트를 만드는 별도의 메소드로 뽑기

## 1.4.3 제어권의 이전을 통한 제어관계 역전
> 제어의 역전
간단히 프로그램의 제어 흐름 구조가 뒤빠뀌는 것

### 일반적인 프로그램의 흐름
- 모든 종류의 작업을 사용하는 쪽에서 제어한다. 
```java
public class UserDao {

    private ConnectionMaker connectionMaker;

    public UserDao() {
        // UserDao가 자신이 사용할 ConnectionMaker 구현체를 결정하는 부분
        this.connectionMaker = new DConnectionMaker();
    }
     public void add(User user) throws ClassNotFoundException, SQLException    {
        Connection c = connectionMaker.makeConnection();
        ...
     }
     public User get(String id) throws ClassNotFoundException, SQLException{
        Connection c = connectionMaker.makeConnection();
     }
     
}
```
- UserDao가 자신이 사용할 ConnectionMaker의 구현 클래스를 자신이 결정하고, 그 오브젝트를 필요한 시점에 생성해 각 메소드에서 이것을 사용한다. 


### 저 윗 부분이 제어의 역전이라는 개념을 도입하면
```java
public class UserDao {

    private ConnectionMaker connectionMaker;

    public UserDao(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }
    public void add(User user) throws ClassNotFoundException, SQLException    {
        Connection c = connectionMaker.makeConnection();
        ...
     }
     public User get(String id) throws ClassNotFoundException, SQLException{
        Connection c = connectionMaker.makeConnection();
     }
}
```
- UserDao도 이제 능동적이 아니라 수동적인 존재가 되었다.
- UserDao 자신도 어떤 ConnectionMaker 구현체가 사용될지를 모르고, 자신이 사용할 오브젝트도 DaoFactory가 공급해주는 대로 사용해야 된다. 

```java
public class UserDaoTest {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        UserDao dao = new DaoFactory().userDao();
	}
}
```
- UserDaoTest도 DaoFactory가 공급해주는 ConnectionMaker를 사용해야 된다.

### 제어의 역전과 프레임워크
- 제어의 역전에서는 오브젝트가 자신이 사용할 오브젝트를 스스로 선택, 생성하지 않는다.
- 또한 자신(UserDao)도 어떻게 만들어지고 어디서 사용되는지를 알 수 없다.
- 모든 제어 권한을 자신이 아닌 다른 대상에게 위임한다.
- 제어의 역전 개념이 적용되어야 프레임워크라고 불린다.
- 제어의 역전에서는 프레임워크 또는 컨테이너와 같이 애플리케이션 컴포넌트의 생성과 관계설정, 사용, 생명주기 관리 들을 관장하는 존재가 필요하다.


# 1.5 스프링의 IoC
## 1.5.1 오브젝트 팩토리를 이용한 스프링 IoC
### 애플리케이션 컨텍스트와 설정정보
- bean
	- 스프링에서는 스프링이 제어권을 가지고 직접 만들고 관계를 부여하는 오브젝트를 Bean이라고함.
	- 스프링 컨테이너가 생성, 관계 설정, 사용 등을 제어해주는 IoC이 적용된 오브젝트
    
- bean factory
	- 빈의 생성과 관계설정 같은 제어를 담당하는 IoC 오브젝트
	- 이것을 좀 더 확장한 것이 **애플리케이션 컨텍스트**
### 여기서 토비님은 앞으로 책 내에서 용어적 약속을 하셨는데
- 책에서 빈팩토리라고 말할 때는 
	- 빈을 생성하고 관계를 설정하는 IoC의 기본 기능에 초점을 맞춤
- 애플리케이션 컨텍스트라고 말할 때는
	- 애플리케이션 전반에 걸쳐 모든 구성요소의 제어 작업을 담당하는 IoC엔진
    
### DaoFactory를 사용하는 애플리케이션 컨텍스트
DaoFactory.java
```java
@Configuration
public class DaoFactory {
    @Bean
    public UserDao userDao(){
        return new UserDao(connectionMaker());
    }

    @Bean
    public ConnectionMaker connectionMaker(){
        return new DConnectionMaker();
    }
}
```
- @Configuration : 오브젝트 설정을 담당하는 클래스
- @Bean : 오브젝트를 만들어주는 메소드에 붙임
- 자바 코드의 탈을 쓰고 있지만, XML과 같은 스프링 전용 설정정보라고 보는 것이 좋다.

UserDaoTest.java
```java
public class UserDaoTest {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        UserDao dao = context.getBean("userDao", UserDao.class);
        ...
        dao.add(user);
   	}
}
```
- getBean(빈의 이름, 리턴 타입)

### 애플리케이션 컨텍스트의 동작 방식
- 애플리케이션 컨텍스트는 Ioc 컨테이너, 스프링 컨테이너, 빈 팩토리라고도 불림

![](https://velog.velcdn.com/images/joohyun333/post/6b87cbe0-c36a-4bed-b94e-a9ec5914a461/image.jpg)

1) DaoFactory 클래스를 설정정보로 등록
2) @Bean이 붙은 메소드의 이름을 가져와 빈 목록을 만듬
3) 클라이언트가 애플리케이션 컨텍스트의 getBean() 메소드를 호출 
4) 자신의 빈 목록에서 요청한 이름이 있는지 탐색, 있으면 빈을 생성하는 메소드를 호출
5) 오브젝트를 생성시킨 후 클라이언트에게 돌려줌
 
### 그럼 굳히 앞에서 만든 DaoFactory같은 오브젝트 팩토리보다 스프링을 적용하면 더 우월한 점이 무엇일까?  

**애플리케이션 컨텍스트를 사용했을 때 얻을 수 있는 장점**
- **클라이언트는 구체적인 팩토리 클래스를 알 필요가 없다.**
	- 사용자가 필요할 때마다 팩토리 오브젝트를 생성해야 된다. 애플리케이션 컨텍스트를 사용하면 일관된 방식으로 오브젝트를 가져올 수 있다 또한 자바 코드보다 XML로 더 단순하게 IoC 설정 정보를 만들 수 있다. 
- **애플리케이션 컨텍스트는 종합 IoC 서비스를 제공해준다.** 
	- 단지 오브젝트 생성, 다른 오브젝트와의 관계설정만이 전부가 아니다. 오브젝트를 활용할 수 있는 다양한 기능을 제공한다. ex) 자동 생성, 인터셉팅, 오브젝트 후처리 등
- **애플리케이션 컨텍스트는 빈을 검색하는 다양한 방법을 제공한다.**
	- 빈의 이름으로 빈을 찾거나 타입, 애노테이션으로 빈을 찾을 수 있다. 
    
## 1.5.3 스프링 IoC의 용어 정리
- **빈** : 스프링이 직접 IoC방식으로 생성과 제어를 담당하는 오브젝트
- **빈 팩토리** : 스프링의 IoC 를 담당하는 핵심 컨테이너, 빈을 관리(등록, 생성, 조회, 반환)한다.
- **애플리케이션 컨텍스트** : 빈 팩토리를 확장한 IoC 컨테이너. 빈 팩토리 + 스프링이 제공하는 애플리케이션 지원 기능
- **설정 정보/ 설정 메타정보** : 애플리케이션 컨텍스트(빈 팩토리)가 IoC를 적용하기 위해 사용하는 메타정보
- 컨테이너 또는 IoC 컨테이너 : IoC 방식으로 빈을 관리한다.

# 1.6 싱글톤 레지스트리와 오브젝트 스코프
> 오브젝트의 동일성과 동등성
- 동일성 : == (메조리 주소가 같음)
- 동등성 : equals() (정보가 같음)

## 1.6.1 싱글톤 리지스트리로서의 애플리케이션 컨텍스트
애플리케이션은 IoC 컨테이너면서 싱글톤을 저장, 관리하는 싱글톤 레지스트리(singleton registry)다. 스프링은 default로 빈 오브젝트를 모두 싱글톤으로 생성한다.

### 서버 애플리케이션 싱글톤
>싱글톤으로 만드는 이유 
- 스프링이 주로 적용되는 대상이 자바 엔터프라이즈 기술을 사용하는 **"서버"환경**.
- 수많은 요청에 따른 오브젝트의 생성은 서버의 부하를 일으킴.
- 서블릿은 멀티 스레드환경에서 싱글톤으로 동작하는 엔터프라이즈 기술의 기본이 되는 서비스 오브젝트

### 싱글톤 패턴의 한계
```java
public class UserDao{
	private static UserDao INSTANCE;
   	... 
    private UserDao(ConnectionMaker connectionMaker){
    this.connectionMaker = connectionMaker;
	}
    public static synchronized UserDao getInstance(){
    	if (INSTANCE == null) INSTANCE = new UserDao(...);
        return INSTANCE;
    }
}
```
- private 생성자를 갖고있기 때문에 상속 X
	- 다른 생성자가 없는 경우 상속과 다형성을 적용 X
	- 상속과 다형성이 적용되지 않는 static 필드, 메소드를 사용
- 싱글톤은 테스트가 어렵다. 
	- 싱글톤을 만드는 방식이 제한적이므로, MOCK 오브젝트로 대체가 힘듬
	- 초기화 과정에서 사용할 오브젝트를 동적으로 주입하기도 힘들기 때문
- 서버환경에서는 싱글톤이 하나만 만들어지는 것을 보장하지 못한다.
	- 서버에서 클래스 로더를 어떻게 구현하냐에 따라 싱글톤이라도 하나 이상의 오브젝트가 만들어질 수 있다. 
	- 여러 JVM이 분산돼서 설치 되는 경우에도 각각 독립적으로 오브젝트가 생긴다. 
- 싱글톤의 사용은 전역 상태를 만들 수 있기 때문에 바람직하지 못하다.
	- 싱글톤의 static method를 이용해 언제든지 쉽게 접근 가능 -> 전역상태로 유지
	- 아무 객체나 자유롭게 접근하고 수정하고 공유할 수 있는 전역 상태를 갖는 것은 객체지향에서 권장되지 않음.
    
    
### 싱글톤 레지스트리
- 스프링인 서버환경에서 싱글톤이 만들어져서 서비스 오브젝트로 사용되는것은 지지
- 자바의 싱글톤 패턴 구현 방식은 단점이 많음.

> 스프링은 직접 싱글톤 형태의 오브젝트를 만들고 관리하는 기능을 제공 > 싱글톤 레지스트리


    

package springbook.user.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springbook.user.domain.Level;
import springbook.user.domain.User;

/*
 * UserDaoTest는 UserDao와 ConnectionMaker 구현 클래스와의 런타임 오브젝트 의존 관계를 설정하는 책임을 담당한다.
 */

//테스트에서 필요로 하는 애플리케이션 컨텍스트를 만들어서 모든 테스트가 공유하게 할 수 있다.(=하나의 테스트 클래스 내의 테스트 메서드는 같은 애플리케이션 컨텍스트를 공유)
//@RunWith은 JUnit 프레임워크의 테스트 실행 방법을 확장할 때 사용하는 애노테이션이다.
//@ContextConfiguration은 자동으로 만들어줄 애플리케이션 컨텍스트의 설정파일 위치를 지정한 것이다.
@RunWith(SpringJUnit4ClassRunner.class)//SpringJUnit4ClassRunner라는 JUnit용 테스트 컨텍스트 프레임워크 확장 클래스를 지정해주면 JUnit이 테스트를 진행하는
									   //중에 테스트가 사용할 애플리케이션 컨텍스트를 만들고 관리하는 작업을 진행한다.
@ContextConfiguration(locations="/springbook/user/dao/applicationContext.xml")
public class UserDaoTest {
	
//	@Autowired
//	private ApplicationContext context;
	@Autowired
	private UserDao dao;
	@Autowired
	private DataSource dataSource;
	private User user1;
	private User user2;
	private User user3;
	
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		//애노테이션 기반의 애플리케이션 컨텍스트 생성방법
//		ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
		
		//XML을 이용하는 애플리케이션 컨텍스트 생성
		/*
		 * 1.GenericXmlApplicationContext는 클래스패스뿐만 아니라 다양한 소스로부터 설정파일을 읽어올 수 있다.
		 * 2.ClassPathXmlApplicationContext는 XML파일을 클래스패스에서 가져올 때 사용할 수 있는 편리한 기능이 추가된 것이다.
		 *   ClassPathXmlApplicationContext의 기능 중에는 클래스 패스의 경로정보를 클래스에서 가져오게 하는 것이 있다.
		 *   Xml파일과 같은 클래스패스에 있는 클래스 오브젝트를 넘겨서 클래스패스에 대한 힌트를 제공할 수 있다.
		 *   예를 들어, UserDao는 springbook.user.dao패키지에 있으므로 daoContext.xml과 같은 클래스패스 위에 있다. 이 UserDao를 함께 넣어주면
		 *   Xml파일의 위치를 UserDao의 위치로부터 상대적으로 지정할 수 있다.
		 *   Ex) new ClassPathXmlApplicationContext("daoContext.xml",UserDao.class);
		 */
//		ApplicationContext context = new GenericXmlApplicationContext("springbook/user/dao/applicationContext.xml"); //생성자에는 applicationContext.xml의 클래스패스를 넣는다.
//		UserDao dao = context.getBean("userDao", UserDao.class);
//		
//		User user = new User();
//		user.setId("kshyun");
//		user.setName("김승현");
//		user.setPassword("aa123");
//		
//		dao.add(user);
//		
//		System.out.println(user.getId() + " 등록 성공");
//		
//		User user2 = dao.get(user.getId());
		
//		System.out.println(user2.getName());
//		System.out.println(user2.getPassword());
//		System.out.println(user2.getId() + " 조회 성공");
		
//		if(!user.getName().equals(user2.getName())) {
//			System.out.println("테스트 실패(name)");
//		}else if(!user.getPassword().equals(user2.getPassword())) {
//			System.out.println("테스트 실패(password)");
//		}else {
//			System.out.println("조회 테스트 성공");
//		}
		
		/*
		 * JUnit 테스트 실행
		 */
		JUnitCore.main("springbook.user.dao.UserDaoTest");
		
	}
	
	/*
	 * JUnit이 제공하는 애노테이션. @Test메서드가 실행되기 전에 먼저 실행해야 하는 메서드를 정의한다.
	 */
	@Before
	public void setUp() {
//		ApplicationContext context = new GenericXmlApplicationContext("/springbook/user/dao/applicationContext.xml");
//		this.dao = context.getBean("userDao", UserDao.class);
		
		this.user1 = new User("gyumee", "박성철", "springno1", Level.BASIC, 1, 0, "parksungchul@google.com");
		this.user2 = new User("leegw700", "이길원", "springno2", Level.SILVER, 55, 10, "leegw@naver.com");
		this.user3 = new User("bumjin", "박범진", "springno3", Level.GOLD, 100, 40, "parkbumjin@daum.net");
	}
	
	/*
	 * 동일한 결과를 보장하는 테스트
	 * 단위 테스트는 코드가 바뀌지 않는다면 매번 실행할 때마다 동일한 테스트 결과를 얻을 수 있어야 한다.
	 */
	@Test
	public void addAndGet() throws SQLException {
		
//		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml", this.getClass());
		
//		UserDao dao = context.getBean("userDao", UserDao.class);
//		User user = new User();
//		user.setId("gyumee");
//		user.setName("김수현");
//		user.setPassword("springno1");
//		User user1 = new User("gyumee", "박성철", "springno1");
//		User user2 = new User("kshyun", "김승현", "springno2");
		
		dao.deleteAll();
		assertThat(dao.getCount(),is(0));
		
		
		dao.add(user1);
		dao.add(user2);
		assertThat(dao.getCount(),is(2));
		
		//get()메서드에 대한 테스트 검증
		User userGet1 = dao.get(user1.getId());
		checkSameUser(userGet1, user1);
		
		User userGet2 = dao.get(user2.getId());
		checkSameUser(userGet2, user2);
	}
	
	/*
	 * JUnit은 특정한 테스트 메서드의 실행 순서를 보장해주지 않는다. 테스트의 결과가 테스트 실행순서에 영향을 받는다면 테스트를 잘못 만든것이다.
	 */
	//getCount()메서드에 대한 테스트
	@Test
	public void count() throws SQLException {
//		ApplicationContext context = new GenericXmlApplicationContext("springbook/user/dao/applicationContext.xml");
//		UserDao dao = context.getBean("userDao", UserDao.class);
//		User user1 = new User("gyumee", "박성철", "springno1");
//		User user2 = new User("leegw700", "이길원", "springno2");
//		User user3 = new User("bumjin", "박범진", "springno3");
		
		dao.deleteAll();
		assertThat(dao.getCount(),is(0));
		
		dao.add(user1);
		assertThat(dao.getCount(),is(1));
		
		dao.add(user2);
		assertThat(dao.getCount(),is(2));
		
		dao.add(user3);
		assertThat(dao.getCount(),is(3));
	}
	
	/*
	 * @Test에 expected를 추가해놓으면 보통의 테스트와는 반대로, 정상적으로 테스트 메서드를 마치면 테스트가 실패하고, expected에서 지정한 예외가 던져지면 테스트가
	 * 성공한다.
	 */
	@Test(expected=EmptyResultDataAccessException.class)
	public void getUserFailure() throws SQLException {
//		ApplicationContext context = new GenericXmlApplicationContext("/springbook/user/dao/applicationContext.xml");
		
//		UserDao dao = context.getBean("userDao", UserDao.class);
		dao.deleteAll();
		assertThat(dao.getCount(),is(0));
		/*
		 * 테스트를 작성할때는 부정적인 케이스를 먼저 만드는 습관을 들이는게 좋다.
		 */
		dao.get("unknown_id");
	}
	
	@Test
	public void getAll() throws SQLException{
		dao.deleteAll();
		
		/*
		 * getAll()에서 query()의 결과에 손댈 것도 아니면서 굳이 검증코드를 추가할까?
		 * UserDao를 사용하는 쪽의 입장에서 getAll()가 내부적으로 JdbcTemplate을 사용하는지, 개발자가 직접 만든 JDBC코드를 사용하는지 알 수 없고,알 필요도 없다.
		 * getAll()이라는 메서드가 어떻게 동작하는지에만 관심이 있는것이다.(=DB에 데이터가 없다면 getAll()호출시 어떻게 될까??)
		 * 따라서 그 예상되는 결과를 모두 검증하는 게 옳다.
		 */
		List<User> users0 = dao.getAll();
		assertThat(users0.size(), is(0));
		
		dao.add(user1);
		
		List<User> users1 = dao.getAll();
		assertThat(users1.size(), is(1));
		checkSameUser(user1, users1.get(0));
		
		dao.add(user2);
		List<User> users2 = dao.getAll();
		assertThat(users2.size(), is(2));
		checkSameUser(user1, users2.get(0));
		checkSameUser(user2, users2.get(1));
		
		dao.add(user3);
		List<User> users3 = dao.getAll();
		assertThat(users3.size(), is(3));
		checkSameUser(user3, users3.get(0));
		checkSameUser(user1, users3.get(1));
		checkSameUser(user2, users3.get(2));
	}
	
	//User 오브젝트의 내용을 비교하는 검증코드, 테스트에서 반복적으로 사용되므로 분리해놓았다.
	private void checkSameUser(User user1, User user2) {
		assertThat(user1.getId(), is(user2.getId()));
		assertThat(user1.getName(), is(user2.getName()));
		assertThat(user1.getPassword(), is(user2.getPassword()));
		assertThat(user1.getLevel(), is(user2.getLevel()));
		assertThat(user1.getLogin(), is(user2.getLogin()));
		assertThat(user1.getRecommend(), is(user2.getRecommend()));
	}
	
	/*
	 * 중복된 키를 가진 정보를 등록했을 때 어떤 예외가 발생하는지를 확인하기 위한 학습 테스트
	 * (일반적으로 학습테스트는 애플리케이션 코드에 대한 테스트와 분리해서 작성하는게 좋다.)
	 */
	@Test(expected=DuplicateKeyException.class)
	public void duplicateKey() {
		dao.deleteAll();
		dao.add(user1);
		dao.add(user1);
	}
	
	/*
	 * JDBC외의 기술을 사용할 때도 DuplicateKeyException을 발생시키려면 SQLException을 가져와서 직접 예외 전환을 하는 방법을 생각해 볼 수 있다.
	 */
	@Test
	public void SqlExceptionTranslate() {
		dao.deleteAll();
		
		try {
			dao.add(user1);
			dao.add(user1);
		}catch(DuplicateKeyException ex) {
			SQLException sqlEx = (SQLException)ex.getRootCause();
			SQLExceptionTranslator set = new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
			assertThat(set.translate(null, null, sqlEx),is(DuplicateKeyException.class));
		}
	}
	
	
	@Test
	public void update() {
		/*
		 * update()테스트에서 바뀐 로우의 내용만 확인해보면 정상적으로 동작 한 것으로 보인다. 
		 * 현재 update()테스트는 수정할 로우의 내용이 바뀐 것만 확인할 뿐이지,수정하지 않아야 할 로우의 내용이 그대로 남아 있는지는 확인해주지 못한다는 문제가 있다. 
		 * 그래서 사용자를 두명 등록해놓고, 그 중 하나만 수정한 뒤에 수정된 사용자와 수정하지 않은 사용자의 정보를 모두 확인하면 된다.
		 */
		dao.deleteAll();
		
		dao.add(user1);
		dao.add(user2);
		
		//user1이라는 텍스트 픽스쳐는 인스턴스 변수로 만들어놓은 것인데, 이를 직접 변경해도 괜찮을까? 상관없다.
		//테스트 메서드가 실행될 때마다 UserDaoTest오브젝트는 새로 만들어지고, setUp()메서드도 다시 불려서 초기화 될테니 내용을 변경해도 상관없다.
		user1.setName("오민규");
		user1.setPassword("springno6");
		user1.setLevel(Level.GOLD);
		user1.setLogin(1000);
		user1.setRecommend(999);
		dao.update(user1);
		
		User user1update = dao.get(user1.getId());
		checkSameUser(user1, user1update);
		User user2update = dao.get(user2.getId());
		checkSameUser(user2, user2update);
	}
	
	
}
























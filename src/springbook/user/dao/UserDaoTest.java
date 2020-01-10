package springbook.user.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;

import springbook.user.domain.User;

/*
 * UserDaoTest는 UserDao와 ConnectionMaker 구현 클래스와의 런타임 오브젝트 의존 관계를 설정하는 책임을 담당한다.
 */
public class UserDaoTest {
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
	 * 동일한 결과를 보장하는 테스트
	 * 단위 테스트는 코드가 바뀌지 않는다면 매번 실행할 때마다 동일한 테스트 결과를 얻을 수 있어야 한다.
	 */
	@Test
	public void addAndGet() throws SQLException {
		
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml", this.getClass());
		
		UserDao dao = context.getBean("userDao", UserDao.class);
//		User user = new User();
//		user.setId("gyumee");
//		user.setName("김수현");
//		user.setPassword("springno1");
		User user1 = new User("gyumee", "박성철", "springno1");
		User user2 = new User("kshyun", "김승현", "springno2");
		
		dao.deleteAll();
		assertThat(dao.getCount(),is(0));
		
		
		dao.add(user1);
		dao.add(user2);
		assertThat(dao.getCount(),is(2));
		
		//get()메서드에 대한 테스트 검증
		User userGet1 = dao.get(user1.getId());
		assertThat(userGet1.getName(), is(user1.getName()));
		assertThat(userGet1.getPassword(),is(user1.getPassword()));
		
		User userGet2 = dao.get(user2.getId());
		assertThat(userGet2.getName(),is(user2.getName()));
		assertThat(userGet2.getPassword(),is(user2.getPassword()));
	}
	
	/*
	 * JUnit은 특정한 테스트 메서드의 실행 순서를 보장해주지 않는다. 테스트의 결과가 테스트 실행순서에 영향을 받는다면 테스트를 잘못 만든것이다.
	 */
	//getCount()메서드에 대한 테스트
	@Test
	public void count() throws SQLException {
		ApplicationContext context = new GenericXmlApplicationContext("springbook/user/dao/applicationContext.xml");
		UserDao dao = context.getBean("userDao", UserDao.class);
		User user1 = new User("gyumee", "박성철", "springno1");
		User user2 = new User("leegw700", "이길원", "springno2");
		User user3 = new User("bumjin", "박범진", "springno3");
		
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
		ApplicationContext context = new GenericXmlApplicationContext("/springbook/user/dao/applicationContext.xml");
		
		UserDao dao = context.getBean("userDao", UserDao.class);
		dao.deleteAll();
		assertThat(dao.getCount(),is(0));
		/*
		 * 테스트를 작성할때는 부정적인 케이스를 먼저 만드는 습관을 들이는게 좋다.
		 */
		dao.get("unknown_id");
	}
	
}
























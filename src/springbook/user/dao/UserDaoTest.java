package springbook.user.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

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
		dao.deleteAll();
		assertThat(dao.getCount(),is(0));
		
		User user = new User();
		user.setId("gyumee");
		user.setName("김수현");
		user.setPassword("springno1");
		
		dao.add(user);
		assertThat(dao.getCount(),is(1));
		
		User user2 = dao.get(user.getId());
		
		assertThat(user2.getName(), is(user.getName()));
		assertThat(user.getPassword(),is(user2.getPassword()));
	}
	
}
























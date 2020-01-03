package springbook.user.dao;

import java.sql.SQLException;

import springbook.user.domain.User;

/*
 * UserDaoTest는 UserDao와 ConnectionMaker 구현 클래스와의 런타임 오브젝트 의존 관계를 설정하는 책임을 담당한다.
 */
public class UserDaoTest {
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		
//		ConnectionMaker connectionMaker = new DConnectionMaker();
//		UserDao dao = new UserDao(connectionMaker);
		
		UserDao dao = new DaoFactory().userDao();
		
		User user = new User();
		user.setId("kshyun");
		user.setName("김승현");
		user.setPassword("aa123");
		
		dao.add(user);
		
		System.out.println(user.getId() + " 등록 처리 됨");
		
		User user2 = dao.get(user.getId());
		System.out.println(user2.getName());
		System.out.println(user2.getPassword());
		
		System.out.println(user2.getId() + " 조회 성공");
	}
	
}

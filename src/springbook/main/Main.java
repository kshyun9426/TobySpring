package springbook.main;

import java.sql.SQLException;

import springbook.user.dao.UserDao;
import springbook.user.domain.User;

public class Main {
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException{
		
		UserDao dao = new UserDao();
		
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

package springbook.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import springbook.user.domain.User;

public class UserDao {
	
	private SimpleConnectionMaker simpleConnectionMaker;
	
	public UserDao() {
		simpleConnectionMaker = new SimpleConnectionMaker();
	}
	
	//users 테이블에 user정보 등록
	public void add(User user) throws ClassNotFoundException, SQLException {
//		Class.forName("com.mysql.jdbc.Driver");
//		Connection c = DriverManager.getConnection("jdbc:mysql://localhost/springbook", "spring", "book");
		
//		Connection c = getConnection(); //추출한 메서드를 호출
		
		Connection c= simpleConnectionMaker.makeNewConnection(); //독립적으로 만든 클래스를 사용
		
		PreparedStatement ps = c.prepareStatement("insert into users(id,name,password) values(?,?,?)");
		ps.setString(1, user.getId());
		ps.setString(2, user.getName());
		ps.setString(3, user.getPassword());
		ps.executeUpdate();
		
		ps.close();
		c.close();
	}
	
	//users테이블에서 user정보 조회
	public User get(String id) throws ClassNotFoundException, SQLException {
//		Class.forName("com.mysql.jdbc.Driver");
//		Connection c = DriverManager.getConnection("jdbc:mysql://localhost/springbook", "spring", "book");
		
//		Connection c = getConnection(); //추출한 메서드를 호출
		
		Connection c= simpleConnectionMaker.makeNewConnection();
		
		PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
		ps.setString(1,id);
		
		ResultSet rs = ps.executeQuery();
		rs.next();
		User user = new User();
		user.setId(rs.getString("id"));
		user.setName(rs.getString("name"));
		user.setPassword(rs.getString("password"));
		
		rs.close();
		ps.close();
		c.close();
		
		return user;
	}
	
	//공통의 기능을 담당하는 메서드로 중복된 코드를 뽑아내는 것을 리팩토링에서는 메서드추출(Method Extract)라고 한다.
	//DB연결 기능이 필요하면 getConnection() 메서드를 이용하게 된다.
	//중복된 코드를 독립적인 메서드로 만들어서 중복을 제거했다.
	//관심의 종류에 따라 코드를 구분해놓았기 때문에 한 가지 관심에 대한 변경이 일어날 경우 그 관심이 집중되는 부분의 코드만 수정하면 된다.
//	private Connection getConnection() throws ClassNotFoundException, SQLException {
		//DB종류와 접속 방법이 바뀌어서 드라이버 클래스와 URL이 바뀌었다거나, 로그인 정보가 변경돼도 앞으로는 getConnection()이라는 메서드의 코드만 수정하면 된다.
//		Class.forName("com.mysql.jdbc.Driver"); 
//		Connection c = DriverManager.getConnection("jdbc:mysql://localhost/springbook", "spring", "book");
//		return c;
//	}
	
	//구현코드는 제거되고 추상 메서드로 바뀌었다. 메서드의 구현은 서브클래스가 담당한다.
//	public abstract Connection getConnection() throws ClassNotFoundException, SQLException;
	
}
























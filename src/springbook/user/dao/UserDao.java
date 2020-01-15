package springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;

import springbook.user.domain.User;


public class UserDao {
	
//	private SimpleConnectionMaker simpleConnectionMaker;
	
	
//	private ConnectionMaker connectionMaker;//인터페이스를 통해 오브젝트에 접근하므로 구체적인 클래스 정보를 알 필요가 없다
	private DataSource dataSource;
	
	private JdbcContext jdbcContext;
	
	
	
//	public UserDao() {
//		simpleConnectionMaker = new SimpleConnectionMaker();
//		connectionMaker = new DConnectionMaker();
//	}
	
//	public UserDao(ConnectionMaker connectionMaker) {
//		this.connectionMaker = connectionMaker;
//	}
	
	
//	public ConnectionMaker getConnectionMaker() {
//		return connectionMaker;
//	}
//
//	public void setConnectionMaker(ConnectionMaker connectionMaker) {
//		this.connectionMaker = connectionMaker;
//	}
	
	public void setJdbcContext(JdbcContext jdbcContext) {
		this.jdbcContext = jdbcContext;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	//users 테이블에 user정보 등록
	public void add(final User user) throws SQLException {
//		Class.forName("com.mysql.jdbc.Driver");
//		Connection c = DriverManager.getConnection("jdbc:mysql://localhost/springbook", "spring", "book");
		
//		Connection c = getConnection(); //추출한 메서드를 호출
		
//		Connection c= simpleConnectionMaker.makeNewConnection(); //독립적으로 만든 클래스를 사용
		
//		Connection c= connectionMaker.makeConnection(); //인터페이스에 정의된 메서드를 사용하므로 클래스가 바뀐다고 해도 메서드 이름이 변경될 걱정은 없다.
		
		//로컬클래스(=메서드레벨에 정의되는 클래스) 생성
		//클래스가 내부 클래스이기 때문에 자신이 선언된 곳의 정보에 접근할 수 있다는 점
//		class AddStatement implements StatementStrategy {
//
//			@Override
//			public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
//				PreparedStatement ps = c.prepareStatement("insert into users(id,name,password) values(?,?,?)");
//				ps.setString(1, user.getId());
//				ps.setString(2, user.getName());
//				ps.setString(3, user.getPassword());
//				return ps;
//			}
//		}
		
		//익명 내부 클래스로 구현
		//클래스 선언과 오브젝트 생성이 결합된 형태로 만들어지며, 상속할 클래스나 구현할 인터페이스를 생성자 대신 사용해서 사용
		this.jdbcContext.workWithStatementStrategy((new StatementStrategy() {
			@Override
			public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
				PreparedStatement ps = c.prepareStatement("insert into users(id,name,password) value(?,?,?)");
				ps.setString(1, user.getId());
				ps.setString(2, user.getName());
				ps.setString(3, user.getPassword());
				return ps;
			}
		}));
	}
	
	

	//users테이블에서 user정보 조회
	public User get(String id) throws SQLException {
//		Class.forName("com.mysql.jdbc.Driver");
//		Connection c = DriverManager.getConnection("jdbc:mysql://localhost/springbook", "spring", "book");
		
//		Connection c = getConnection(); //추출한 메서드를 호출
		
//		Connection c= simpleConnectionMaker.makeNewConnection();
		
//		Connection c= connectionMaker.makeConnection();
		Connection c = dataSource.getConnection();
		
		PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
		ps.setString(1,id);
		
		ResultSet rs = ps.executeQuery();
		User user = null;
		if(rs.next()) {
			user = new User();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
		}
		rs.close();
		ps.close();
		c.close();
		
		if(user == null)
			throw new EmptyResultDataAccessException(1);
		
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
	
	
	public void deleteAll() throws SQLException {
//		StatementStrategy st = new DeleteAllStatement(); //선정한 전략 클래스의 오브젝트 생성
		this.jdbcContext.workWithStatementStrategy((new StatementStrategy() {
			@Override
			public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
				return c.prepareStatement("delete from users");
			}
		})); //컨텍스트호출. 전략 오브젝트 전달
	}
	
	//컨텍스트에 해당하는 부분을 별도 메서드로 추출
//	public void jdbcContextWithStatementStrategy(StatementStrategy stmt) throws SQLException {
//		
//		Connection c = null;
//		PreparedStatement ps = null;
//		
//		try {
//			c = dataSource.getConnection();
//			ps = stmt.makePreparedStatement(c);
//			ps.executeUpdate();
//		}catch(SQLException e) {
//			throw e;
//		}finally {
//			if(ps != null) {
//				try {
//					ps.close();
//				}catch(SQLException e) {
//					
//				}
//			}
//			if(c != null) {
//				try {
//					c.close();
//				}catch(SQLException e) {
//				}
//			}
//		}
//	}
	
	public int getCount() throws SQLException {
		
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			c = dataSource.getConnection();
			ps = c.prepareStatement("select count(*) from users");
			rs = ps.executeQuery();
			rs.next();
			return rs.getInt(1);
		}catch(SQLException e) {
			throw e;
		}finally {
			if(rs != null) {
				try {
					rs.close();
				}catch(SQLException e) {
				}
			}
			if(ps != null) {
				try {
					ps.close();
				}catch(SQLException e) {
				}
			}
			if(c != null) {
				try {
					c.close();
				}catch(SQLException e) {
				}
			}
		}
	}
	
}
























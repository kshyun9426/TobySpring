package springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import springbook.user.domain.Level;
import springbook.user.domain.User;

public class UserDaoJdbc implements UserDao{
	
//	private SimpleConnectionMaker simpleConnectionMaker;
//	private ConnectionMaker connectionMaker;//인터페이스를 통해 오브젝트에 접근하므로 구체적인 클래스 정보를 알 필요가 없다
//	private DataSource dataSource;
//	private JdbcContext jdbcContext;
	private JdbcTemplate jdbcTemplate;
	
	private RowMapper<User> userMapper = new RowMapper<User>() {
		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
			user.setLevel(Level.valueOf(rs.getInt("level")));
			user.setLogin(rs.getInt("login"));
			user.setRecommend(rs.getInt("recommend"));
			return user;
		}
	};
	
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
	
//	public void setJdbcContext(JdbcContext jdbcContext) {
//		this.jdbcContext = jdbcContext;
//	}

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
//		this.dataSource = dataSource; //DataSource는 JdbcTemplate을 만든 후에는  사용하지 않으니 저장해두지 않아도 된다.
	}

	//users 테이블에 user정보 등록
	public void add(User user) {
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
//		this.jdbcContext.workWithStatementStrategy((new StatementStrategy() {
//			@Override
//			public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
//				PreparedStatement ps = c.prepareStatement("insert into users(id,name,password) value(?,?,?)");
//				ps.setString(1, user.getId());
//				ps.setString(2, user.getName());
//				ps.setString(3, user.getPassword());
//				return ps;
//			}
//		}));
		
		this.jdbcTemplate.update("insert into users(id,name,password,level,login,recommend) value(?,?,?,?,?,?)",
				user.getId(), user.getName(), user.getPassword(),user.getLevel().intValue(),user.getLogin(),user.getRecommend());
	}
	
	

	//users테이블에서 user정보 조회
//	public User get(String id) throws SQLException {
//		Class.forName("com.mysql.jdbc.Driver");
//		Connection c = DriverManager.getConnection("jdbc:mysql://localhost/springbook", "spring", "book");
		
//		Connection c = getConnection(); //추출한 메서드를 호출
		
//		Connection c= simpleConnectionMaker.makeNewConnection();
		
//		Connection c= connectionMaker.makeConnection();
//		Connection c = dataSource.getConnection();
//		
//		PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
//		ps.setString(1,id);
//		
//		ResultSet rs = ps.executeQuery();
//		User user = null;
//		if(rs.next()) {
//			user = new User();
//			user.setId(rs.getString("id"));
//			user.setName(rs.getString("name"));
//			user.setPassword(rs.getString("password"));
//		}
//		rs.close();
//		ps.close();
//		c.close();
//		
//		if(user == null)
//			throw new EmptyResultDataAccessException(1);
//		
//		return user;
//	}
	
	/*
	 * queryForObject메서드의 첫번째 파라미터는 PreparedStatement를 만들기 위한 SQL이고, 두번째는 여기에 바인딩할 값들이다. update()에서처럼 가변인자를 사용하면
	 * 좋겠지만 뒤에 다른 파라미터가 있기 때문에 이 경우엔 가변인자 대신에 Object타입 배열을 사용해야 한다.
	 * queryForObject내부에서 이 두가지 파라미터를 사용하는 PreparedStatement콜백이 만들어질것이다.
	 */
	public User get(String id) {
		//queryForObject()는 실행하면 한개의 로우만 얻을 것이라고 기대한다.(2개이상 로우가 조회되면 안됨)
		//그리고 ResultSet의 next()를 실행해서 첫 번째 로우로 이동시킨 후에 RowMapper콜백을 호출한다. 이미 RowMapper가 호출되는 시점에서 ResultSet은 첫 번째
		//로우를 가리키고 있으므로 다시 rs.next()를 호출할 필요가 없다.
		return this.jdbcTemplate.queryForObject(
				"select * from users where id = ?", 
				new String[] {id},
				this.userMapper);
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
	
	
	public void deleteAll() {
		//JdbcTemplate클래스의 update()메서드 사용
		this.jdbcTemplate.update(
			new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					return con.prepareStatement("delete from users");
				}
			}
		);
		
//		this.jdbcContext.executeSql("delete from users"); //변하는 SQL문장
	}
	
	//복잡한 익명 내부 클래스의 사용을 최소화할 수 있는 방법으로 중복될 가능성이 있는 자주 바뀌디 않는 부분을 분리했다.
	//이렇게 하면 복잡한 익명 내부클래스인 콜백을 직접 만들 필요조차 없어졌다.
	//executeSql()는 UserDao만 사용하기에는 아깝다 재사용 가능한 콜백을 담고 있는 메서드라면 DAO가 공유할 수 있는 탬플릿 클래스(=JdbcContext) 안으로 옮겨도 된다.
//	private void executeSql(final String query) throws SQLException { //변하지 않는 콜백 클래스 정의와 오브젝트 생성
//		this.jdbcContext.workWithStatementStrategy(
//			new StatementStrategy() {
//				@Override
//				public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
//					return c.prepareStatement(query);
//				}
//			}
//		);
//	}
	
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
	
//	public int getCount() throws SQLException {
//		Connection c = null;
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//		try {
//			c = dataSource.getConnection();
//			ps = c.prepareStatement("select count(*) from users");
//			rs = ps.executeQuery();
//			rs.next();
//			return rs.getInt(1);
//		}catch(SQLException e) {
//			throw e;
//		}finally {
//			if(rs != null) {
//				try {
//					rs.close();
//				}catch(SQLException e) {
//				}
//			}
//			if(ps != null) {
//				try {
//					ps.close();
//				}catch(SQLException e) {
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
	
	//getCount()는 SQL쿼리를 실행하고 ResultSet을 통해 결과값을 가져오는 코드다. 이런 작업 흐름을 가진 코드에서 사용할 수 있는 템플릿은 PreparedStatement콜백과 
	//ResultSetExtractor콜백을 파라미터로 받는 query()메서드이다.
	public int getCount() {
		return this.jdbcTemplate.query(new PreparedStatementCreator() {	
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				return con.prepareStatement("select count(*) from users");
			}
		//ResultSetExtractor는 ResultSet에서 추출할 수 있는 값의 타입은 다양하기 때문에 타입 파라미터를 사용한것이다.
		},new ResultSetExtractor<Integer>() { //ResultSEtExtractor는 PreparedStatement의 쿼리를 실행해서 얻은 ResultSet을 전달받는 콜백이다.
			@Override
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException { //ResultSet으로부터 값 추출
				rs.next();
				return rs.getInt(1);
			}
		});
	}
	
	//SQL의 실행결과가 하나의 정수값이 되는 경우는 자주 볼수 있다. 클라이언트에서 콜백의 작업을 위해 특별히 제공할 값도 없어서 단순하다. 그래서 ResultSetExtractor콜백을 템플릿
	//안으로 옮겨 재사용할 수 있다. JdbcTemplate은 이런 기능을 가진 콜백을 내장하고 있는 queryForInt()라는 편리한 메서드를 제공한다.
	public int getCount2() {
		return this.jdbcTemplate.queryForInt("select count(*) from users");
	}
	
	public List<User> getAll(){
		return this.jdbcTemplate.query("select * from users order by id", this.userMapper);
	}

	@Override
	public void update(User user) {
		this.jdbcTemplate.update("update users set name=?,password=?,level=?,login=?,recommend=? where id=?"
				,user.getName()
				,user.getPassword()
				,user.getLevel().intValue()
				,user.getLogin()
				,user.getRecommend()
				,user.getId());
	}
	
}

package springbook.user.dao;

import java.sql.Connection;
import java.sql.SQLException;

//인터페이스의 도입(1.3.2)
public interface ConnectionMaker {
	
	public Connection makeConnection() throws ClassNotFoundException, SQLException;
	
}

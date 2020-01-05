package springbook.user.dao;

import java.sql.Connection;
import java.sql.SQLException;
/*
 * 의존 관계 주입 응용(DI의 장점)
 * -부가기능 추가의 용이성
 *  DAO와 DB커넥션을 만드는 오브젝트 사이에 연결횟수를 카운팅하는 오브젝트를 하나 더 추가하는 것
 */
public class CountingConnectionMaker implements ConnectionMaker {

	int counter = 0;
	private ConnectionMaker realConnectionMaker; 
	
	public CountingConnectionMaker(ConnectionMaker realConnectionMaker) {
		this.realConnectionMaker = realConnectionMaker;
	}
	
	/*
	 * CountingConnectionMaker 클래스는 ConnectionMaker인터페이스를 구현했지만 내부에서 직접 DB커넥션을 만들지 않는다.
	 * 대신 DAO가 DB커넥션을 가져올 때마다 호출하는 makeConnection()에서 DB연결횟수 카운터를 증가시킨다.
	 * CountingConnectionMaker는 자신의 관심사인 DB연결횟수 카운팅 작업을 마치면 실제 DB커넥션을 만들어주는 realConnectionMaker에 저장된 ConnectionMaker
	 * 타입 오브젝트의 makeConnection()을 호출해서 그 결과를 DAO에게 돌려준다.
	 * 
	 */
	@Override
	public Connection makeConnection() throws ClassNotFoundException, SQLException {
		this.counter++;
		return realConnectionMaker.makeConnection();
	}
	
	public int getCounter() {
		return this.counter;
	}

}

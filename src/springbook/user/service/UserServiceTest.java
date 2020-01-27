package springbook.user.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/springbook/user/dao/applicationContext.xml")
public class UserServiceTest {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private UserService userService;
	
	List<User> users;
	
	//userService빈의 주입을 확인하는 테스트
	@Test
	public void bean() {
		assertThat(this.userService,is(notNullValue()));
	}
	
	/*
	 * 테스트픽스쳐
	 * 비즈니스 로직을 보면 로그인 횟수와 추천횟수가 각각 기준 값인 50,30이상이 되면 SILVER, GOLD로 업그레이드 된다.
	 * 이럴 때 테스트에 사용할 데이터를 경계가 되는 값의 전후로 선택하는 것이 좋다.
	 */
	@Before
	public void setUp() {
		users = Arrays.asList(
					new User("bumjin", "박범진", "p1", Level.BASIC, 49, 0), //50을 기준으로
					new User("joytouch", "강명선", "p2", Level.BASIC, 50, 0),
					new User("erwins", "신승한", "p3", Level.SILVER, 60, 29),//30을 기준으로
					new User("madnite1", "이상호", "p4", Level.SILVER, 60, 30),
					new User("green", "오민규", "p5", Level.GOLD, 100, 100)
				);
	}
	
	@Test
	public void upgradeLevels() {
		userDao.deleteAll();
		for(User user : users) {
			userDao.add(user);
		}
		
		userService.upgradeLevels();
		
		//각 사용자별로 업그레이드 후의 예상 레벨을 검증한다.
		checkLevel(users.get(0), Level.BASIC);
		checkLevel(users.get(1), Level.SILVER);
		checkLevel(users.get(2), Level.SILVER);
		checkLevel(users.get(3), Level.GOLD);
		checkLevel(users.get(4), Level.GOLD);
	}
	
	private void checkLevel(User user, Level expectedLevel) {
		User userUpdate = userDao.get(user.getId());
		assertThat(userUpdate.getLevel(), is(expectedLevel));
	}
	
	/*
	 * 처음 가입하는 사용자는 기본적으로 BASIC레벨이어야 한다는 부분을 위한 테스트 케이스
	 */
	@Test
	public void add() {
		userDao.deleteAll();
		
		User userWithLevel = users.get(4);
		User userWithoutLevel = users.get(0);
		userWithoutLevel.setLevel(null);
		
		userService.add(userWithLevel);
		userService.add(userWithoutLevel);
		
		User userWithLevelRead = userDao.get(userWithLevel.getId());
		User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());
		
		assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
		assertThat(userWithoutLevelRead.getLevel(), is(Level.BASIC));
	}
	
}
















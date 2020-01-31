package springbook.user.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static springbook.user.service.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserService.MIN_RECOMMEND_FOR_GOLD;

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
import springbook.user.domain.User;;

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
	 * - 테스트픽스쳐
	 * 비즈니스 로직을 보면 로그인 횟수와 추천횟수가 각각 기준 값인 50,30이상이 되면 SILVER, GOLD로 업그레이드 된다.
	 * 이럴 때 테스트에 사용할 데이터를 경계가 되는 값의 전후로 선택하는 것이 좋다.
	 */
	@Before
	public void setUp() {
		users = Arrays.asList(
					//업그레이드 조건인 로그인 횟수와 추천 횟수가 애플리케이션 코드와 테스트 코드에 중복돼서 나타난다.
					//테스트와 애플리케이션 코드에 나타난 이런 숫자의 중복도 제거해줘야 한다. 한 가지 변경 이유가 발생했을 때 여러 군데를 고치게 만든다면 중복이기 때문이다.
					new User("bumjin", "박범진", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0), //50을 기준으로
					new User("joytouch", "강명선", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
					new User("erwins", "신승한", "p3", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD-1),//30을 기준으로
					new User("madnite1", "이상호", "p4", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD),
					new User("green", "오민규", "p5", Level.GOLD, 100, Integer.MAX_VALUE)
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
//		checkLevel(users.get(0), Level.BASIC);
//		checkLevel(users.get(1), Level.SILVER);
//		checkLevel(users.get(2), Level.SILVER);
//		checkLevel(users.get(3), Level.GOLD);
//		checkLevel(users.get(4), Level.GOLD);
		
		//위 의 테스트에는 checkLevel()메서드를 호출할 때 일일이 다음 단계의 레벨이 무엇인지 넣어줬다. 이것도 중복이다. Level이 갖고 있어야 할 다음 레벨이 무엇인가 하는
		//정보를 테스트에 직접 넣어둘 이유가 없다.
		checkLevelUpgraded(users.get(0), false);
		checkLevelUpgraded(users.get(1), true);
		checkLevelUpgraded(users.get(2), false);
		checkLevelUpgraded(users.get(3), true);
		checkLevelUpgraded(users.get(4), false);
	}
	
	//checkLevel()의 개선 전
//	private void checkLevel(User user, Level expectedLevel) {
//		User userUpdate = userDao.get(user.getId());
//		assertThat(userUpdate.getLevel(), is(expectedLevel));
//	}
	
	//checkLevel()의 개선 후
	//각 사용자에 대해 업그레이드를 확인하려는 것인지 아닌지가 좀 더 이해하기 쉽게 true, false로 나타나 있어서 보기 좋다.
	private void checkLevelUpgraded(User user, boolean upgraded) { //어떤 레벨로 바뀔 것인가가 아니라, 다음 레벨로 업그레이드될 것인가를 지정한다.
		User userUpdate = userDao.get(user.getId());
		if(upgraded) {
			assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel())); //다음 레벨이 무엇인지는 Level에게 물어보면 된다.
		}else {
			assertThat(userUpdate.getLevel(), is(user.getLevel()));
		}
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
	
	//강제 예외를 발생 시키기 위한 테스트
	@Test
	public void upgradeAllOrNothing() {
		UserService testUserService = new TestUserService(users.get(3).getId());
		testUserService.setUserDao(this.userDao);
		
		userDao.deleteAll();
		for(User user : users) userDao.add(user);
		
		try {
			testUserService.upgradeLevels();
			fail("TestUserServiceException expected"); //TestUserService는 업그레이드 작업중에 예외가 발생해야 한다. 정상 종료라면 문제가 있으니 실패
		}catch(TestUserServiceException e) { //TestService가 던져주는 예외를 잡아서 계속 진행하도록 한다. 그 외에 예외라면 테스트 실패
			
		}
		
		//예외가 발생하기 전에 레벨 변경이 있었던 사용자의 레벨이 처음 상태로 바뀌었나 확인(핵심)
		checkLevelUpgraded(users.get(1), false);
		
	}
	
	//테스트용 클래스(UserService 대역)
	static class TestUserService extends UserService {
		private String id;
		
		private TestUserService(String id) {
			this.id = id;
		}
		
		protected void upgradeLevel(User user) {
			if(user.getId().equals(this.id)) throw new TestUserServiceException();
			super.upgradeLevel(user);
		}
	}
	
	static class TestUserServiceException extends RuntimeException {
		
	}
}
















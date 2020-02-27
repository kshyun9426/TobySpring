package springbook.user.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static springbook.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserServiceImpl.MIN_RECOMMEND_FOR_GOLD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/springbook/user/dao/test-applicationContext.xml")
public class UserServiceTest {
	
	@Autowired
	private ApplicationContext context;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	UserService userService;
	
	@Autowired
	UserService testUserService; //같은 타입의 빈이 두 개 존재하기 때문에 필드 이름을 기준으로 주입될 빈이 결정된다
	
//	@Autowired
//	private PlatformTransactionManager transactionManager;
	
	@Autowired
	MailSender mailSender;
	
//	@Autowired
//	UserServiceImpl userServiceImpl;
	
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
					new User("bumjin", "박범진", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0, "bumjin@naver.com"), //50을 기준으로
					new User("joytouch", "강명선", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "joy@daum.net"),
					new User("erwins", "신승한", "p3", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD-1, "erwins@google.com"),//30을 기준으로
					new User("madnite1", "이상호", "p4", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD, "madnite1@naver.com"),
					new User("green", "오민규", "p5", Level.GOLD, 100, Integer.MAX_VALUE, "green@google.com")
				);
	}
	
/*
	@Test
	@DirtiesContext //컨텍스트의 DI설정을 변경하는 테스트라는것을 알림
	public void upgradeLevels() throws Exception {
		
		UserServiceImpl userServiceImpl = new UserServiceImpl(); //고립된 테스트에서는 테스트 대상 오브젝트를  직접 생성하면 된다.
		
		MockUserDao mockUserDao = new MockUserDao(this.users);
		userServiceImpl.setUserDao(mockUserDao); //목오브젝트로 만든 UserDao를 직접 DI해준다.
		
		MockMailSender mockMailSender = new MockMailSender();
		userServiceImpl.setMailSender(mockMailSender); //메일 발송 결과를 테스트할 수 있도록 목 오브젝트를 만들어 userService의 의존 오브젝트로 주입해준다.
		
		userServiceImpl.upgradeLevels();
		
		//각 사용자별로 업그레이드 후의 예상 레벨을 검증한다. 
//		checkLevel(users.get(0), Level.BASIC);
//		checkLevel(users.get(1), Level.SILVER);
//		checkLevel(users.get(2), Level.SILVER);
//		checkLevel(users.get(3), Level.GOLD);
//		checkLevel(users.get(4), Level.GOLD);
		
		//위 의 테스트에는 checkLevel()메서드를 호출할 때 일일이 다음 단계의 레벨이 무엇인지 넣어줬다. 이것도 중복이다. Level이 갖고 있어야 할 다음 레벨이 무엇인가 하는
		//정보를 테스트에 직접 넣어둘 이유가 없다.
//		checkLevelUpgraded(users.get(0), false);
//		checkLevelUpgraded(users.get(1), true);
//		checkLevelUpgraded(users.get(2), false);
//		checkLevelUpgraded(users.get(3), true);
//		checkLevelUpgraded(users.get(4), false);
		
		List<User> updated = mockUserDao.getUpdated(); //MockUserDao로부터 업데이트 결과를 가져온다.
		assertThat(updated.size(), is(2));
		checkUserAndLevel(updated.get(0), "joytouch", Level.SILVER); //업데이트 횟수와 정보를 확인한다.
		checkUserAndLevel(updated.get(1), "madnite1", Level.GOLD);
		
		//목 오브젝트에 저장된 메일 수신자 목록을 가져와 업그레이드 대상과 일치하는지 확인
		List<String> request = mockMailSender.getRequests();
		assertThat(request.size(), is(2));
		assertThat(request.get(0), is(users.get(1).getEmail()));
		assertThat(request.get(1), is(users.get(3).getEmail()));
	}
*/
	
	//Mockito 프레임워크를 이용해 만든 테스트 코드
	@Test
	public void mockUpgradeLevels() throws Exception {
		UserServiceImpl userServiceImpl = new UserServiceImpl();
		
		//다이내믹한 목 오브젝트 생성과 메서드의 리턴 값 설정
		UserDao mockUserDao = mock(UserDao.class);
		when(mockUserDao.getAll()).thenReturn(this.users);
		userServiceImpl.setUserDao(mockUserDao);
		
		MailSender mockMailSender = mock(MailSender.class);
		userServiceImpl.setMailSender(mockMailSender);
		
		userServiceImpl.upgradeLevels();
		
		verify(mockUserDao, times(2)).update(any(User.class));
		verify(mockUserDao, times(2)).update(any(User.class));
		verify(mockUserDao).update(users.get(1));
		assertThat(users.get(1).getLevel(), is(Level.SILVER));
		verify(mockUserDao).update(users.get(3));
		assertThat(users.get(3).getLevel(), is(Level.GOLD));
		
		ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(mockMailSender, times(2)).send(mailMessageArg.capture());
		List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
		assertThat(mailMessages.get(0).getTo()[0], is(users.get(1).getEmail()));
		assertThat(mailMessages.get(1).getTo()[0], is(users.get(3).getEmail()));
	}
	
	private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
		assertThat(updated.getId(), is(expectedId));
		assertThat(updated.getLevel(), is(expectedLevel));
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
//	@DirtiesContext //컨텍스트 무효화 애노테이션
	public void upgradeAllOrNothing() throws Exception {
//		TestUserServiceImpl testUserService = new TestUserServiceImpl(users.get(3).getId());
		TestUserServiceImpl testUserService = new TestUserServiceImpl();
		testUserService.setUserDao(this.userDao);
		testUserService.setMailSender(mailSender);
		
//		UserServiceTx txUserService = new UserServiceTx();
//		txUserService.setTransactionManager(transactionManager);
//		txUserService.setUserService(testUserService);
		
		//팩토리 빈 자체를 가져와야 하므로 빈 이름에 &를  반드시 넣어줘야 한다.
//		TxProxyFactoryBean txProxyFactoryBean = context.getBean("&userService", TxProxyFactoryBean.class); //테스트용 타겟 주입
		
		//userService빈은 스프링의 ProxyFactoryBean이다.
//		ProxyFactoryBean txProxyFactoryBean = context.getBean("&userService", ProxyFactoryBean.class); 
//		txProxyFactoryBean.setTarget(testUserService);
//		UserService txUserService = (UserService)txProxyFactoryBean.getObject(); 
													//변경된 타겟 설정을 이용해서 트랜잭션 다이내믹 프록시 오브젝트를 다시 생성한다.
		
		//다이내믹 프록시를 이용한 트랜잭션 테스트
//		TransactionHandler txHandler = new TransactionHandler();
//		txHandler.setTarget(testUserService);
//		txHandler.setTransactionManager(transactionManager);
//		txHandler.setPattern("upgradeLevels");
//		UserService txUserService = (UserService)Proxy.newProxyInstance(getClass().getClassLoader(),
//				new Class[]{UserService.class}, txHandler); //UserService 인터페이스 타입의 다이내믹 프록시 생성
		
		userDao.deleteAll();
		for(User user : users) 
			userDao.add(user);
		
		try {
//			txUserService.upgradeLevels();
			this.testUserService.upgradeLevels();
			fail("TestUserServiceException expected"); //TestUserService는 업그레이드 작업중에 예외가 발생해야 한다. 정상 종료라면 문제가 있으니 실패
		}catch(TestUserServiceException e) { //TestService가 던져주는 예외를 잡아서 계속 진행하도록 한다. 그 외에 예외라면 테스트 실패
			
		}
		
		//예외가 발생하기 전에 레벨 변경이 있었던 사용자의 레벨이 처음 상태로 바뀌었나 확인(핵심)
		checkLevelUpgraded(users.get(1), false);
	}
	
	//테스트용 클래스(UserService 대역)
	static class TestUserServiceImpl extends UserServiceImpl {
//		private String id;
//		
//		private TestUserServiceImpl(String id) {
//			this.id = id;
//		}
		
		private String id = "madnite1"; //테스트코드에서 TestUserServiceImpl클래스를 생성하는 것이 아니기 때문에 일단 하드코딩해서 강제 예외할 대상값 초기화
		
		protected void upgradeLevel(User user) {
			if(user.getId().equals(this.id)) 
				throw new TestUserServiceException();
			super.upgradeLevel(user);
		}
	}
	
	static class TestUserServiceException extends RuntimeException {
		
	}
	
	/*
	 * 목 오브젝트를 이용한 테스트
	 * UserService의 코드가 정상적으로 수행되도록 돕는 역할이 우선이므로 메일을 발송하는 기능은 없다.
	 * 대신 테스트 대상이 넘겨주는 출력 값을 보관해두는 기능을 추가했다.
	 */
	static class MockMailSender implements MailSender {

		private List<String> requests = new ArrayList<>();
		
		public List<String> getRequests(){
			return requests;
		}
		
		@Override
		public void send(SimpleMailMessage mailMessage) throws MailException {
			requests.add(mailMessage.getTo()[0]);
		}

		@Override
		public void send(SimpleMailMessage[] mailMessage) throws MailException {
			
		}
		
	}
	
	/*
	 * UserDao타입의 테스트 대역
	 */
	static class MockUserDao implements UserDao {
		
		private List<User> users;
		private List<User> updated = new ArrayList<>();

		private MockUserDao(List<User> users) {
			this.users = users;
		}
		
		public List<User> getUpdated(){
			return this.updated;
		}
		
		@Override
		public List<User> getAll() {
			return this.users;
		}
		
		@Override
		public void update(User user) {
			updated.add(user);
		}
		
		//인터페이스를 구현하려면 인터페이스 내의 모든 메서드를 만들어줘야 한다는 부담이 있다. 테스트 중에 사용할 것은 제한적인데도 불구하고 말이다.
		//사용하지 않을 메서드도 구현해줘야 한다면 UnsupportedOperationException을 던지도록 만드는 편이 좋다.
		@Override
		public void add(User user) { 
			throw new UnsupportedOperationException(); 
		}

		@Override
		public User get(String id) {
			 throw new UnsupportedOperationException(); 
		}

		@Override
		public void deleteAll() {
			 throw new UnsupportedOperationException(); 
		}

		@Override
		public int getCount() {
			 throw new UnsupportedOperationException(); 
		}
	}
	
	//프록시로 변경된 오브젝트인지 확인한다.
	@Test
	public void advisorAutoProxyCreator() {
		assertThat(testUserService, is(java.lang.reflect.Proxy.class));
	}
}
















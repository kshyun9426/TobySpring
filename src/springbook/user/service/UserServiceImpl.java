package springbook.user.service;

import java.util.List;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

public class UserServiceImpl implements UserService {
	
	//상수의 도입
	public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECOMMEND_FOR_GOLD = 30;
	
	UserDao userDao;
//	private PlatformTransactionManager transactionManager; //PlatformTransactionManager의 경우는 관례적으로 transactionManager라는 이름을 사용
	private MailSender mailSender;
	
//	public void setTransactionManager(PlatformTransactionManager transactionManager) {
//		this.transactionManager = transactionManager;
//	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}
	
	/*
	 * 리팩토링 전 upgradeLevels()메서드의 문제점
	 *  - if/elseif/else블록들이 읽기 불편하다. 레벨의 변화 단계와 업그레이드 조건, 조건이 충족됐을 때 해야 할 작업이 한데 섞여 있어서 로직을 이해하기가 쉽지 않다. 
	 * 
	 */
//	public void upgradeLevels() {
//		List<User> users = userDao.getAll();
//		for(User user : users) {
//			Boolean changed = null;
//			//if문 조건에는 현재 레벨이 무엇인지 파악하는 로직과 업그레이드 조건을 담은 로직이 함께 존재한다.
//			if(user.getLevel() == Level.BASIC && user.getLogin() >= 50) {
//				user.setLevel(Level.SILVER); //다음 단계의 레벨이 무엇이며 업그레이드를 위한 작업은 어떤 것인지가 함께 담겨있다.
//				changed = true; //자체로는 의미가 없고 아래의 판별기준으로 필요하다(분리되어있음)
//			}
//			else if(user.getLevel() == Level.SILVER && user.getRecommend() >= 30) {
//				user.setLevel(Level.GOLD);
//				changed = true;
//			}
//			else if(user.getLevel() == Level.GOLD) {
//				changed = false;
//			}
//			else {
//				changed = false;
//			}
//			
//			if(changed) {
//				userDao.update(user);
//			}
//		}
//	}
	
	/*
	 * 1.upgradeLevels() 리팩토링
	 * 	-추상적인 레벨에서 로직을 작성
	 * 
	 * 2.트랜잭션 동기화 적용
	 * 
	 * 3.2로 인해서 JDBC에 종속적인 Conneciton을 이용한 트랜잭션 코드가 UserService에 등장하면서부터 UserService는 UserDaoJdbc에 간접적으로 의존하는 코드가 돼버림
	 * 
	 */
//	public void upgradeLevels() throws Exception {
//		TransactionSynchronizationManager.initSynchronization(); //트랜잭션 동기화 관리자를 이용해 동기화 작업을 초기화한다.
//		//DB커넥션을 생성하고 트랜잭션을 시작한다. 이후의  DAO작업은 모두 여기서 시작한 트랜잭션 안에서 진행된다.
//		Connection c = DataSourceUtils.getConnection(dataSource); 
//		//DataSourceUtils의 getConnection()은 Connection오브젝트를 생성해 줄 뿐만 아니라 트랜잭션 동기화에 사용하도록 저장소에 바인딩해준다.
//		c.setAutoCommit(false);
//		
//		try {
//			List<User> users = userDao.getAll();
//			for(User user : users) {
//				if(canUpgradeLevel(user)) { //업그레이드가 가능한지 확인하고
//					upgradeLevel(user); //가능하면 업그레이드
//				}
//			}
//			c.commit(); //정상적으로 작업을 마치면 트랜잭션 커밋
//		}catch(Exception e) {
//			c.rollback(); //예외가 발생하면 롤백한다.
//			throw e;
//		}finally {
//			DataSourceUtils.releaseConnection(c,dataSource); //스프링 유틸리티 메서드를 이용해 DB커넥션은 안전하게 닫는다.
//			TransactionSynchronizationManager.unbindResource(this.dataSource); 
//			TransactionSynchronizationManager.clearSynchronization(); //동기화 작업 종료 및 정리
//		}
//	}
	
	//스프링의 트랜잭션 추상화 API를 적용
	/*
	 * 스프링이 제공하는 트랜잭션 경계설정을 위한 추상 인터페이스는 PlatformTransactionManager다.(JDBC의 로컬 트랜잭션을 이용한다면 DataSourceTransactionManager사용)
	 * JDBC를 이용하는 경우에는 먼저 Connection을 생성하고 나서 트랜잭션을 시작했다. 하지만 PlatformTransactionManager에서는 트랜잭션을 가져오는 요청인 getTransaction()
	 * 메서드를 호출하기만 하면 된다. 필요에 따라 트랜잭션 매니저가 DB커넥션을 가져오는 작업도 같이 수행해주기 때문이다. 
	 */
	public void upgradeLevels() {
//		PlatformTransactionManager transactionManager = new DataSourceTransactionManager(); //JDBC트랜잭션 추상 오브젝트 생성 
								//->어떤 트랜잭션 매니저 구현 클래스를 사용할지 UserService코드가 알고 있는 것은 DI원칙에 위배된다.
								//자신이 사용할 구체적인 클래스를 스스로 결정하고 생성하지 말고 컨테이너를 통해 외부에서 제공받게 되는 스프링DI의 방식으로 바꿔야한다.
//		TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition()); //트랜잭션 시작
								//파라미터로 넘기는 DefaultTransactionDefinition 오브젝트는 트랜잭션에 대한 속성을 담고 있다.
								//시작된 트랜잭션은 TransactionStatus타입의 변수에 저장되게 하고, TransactionStatus는 트랜잭션에 대한 조작이 필요할 때 
								//PlatformTransactionManager메서드의 파라미터로 전달해주면 된다.
		//트랜잭션 안에서 진행되는 작업(=비즈니스 로직)
//		try {
		
		List<User> users = userDao.getAll();
		for(User user : users) {
			if(canUpgradeLevel(user)) { //업그레이드가 가능한지 확인하고
				upgradeLevel(user); //가능하면 업그레이드
			}
		}
//			transactionManager.commit(status); //트랜잭션 커밋
//		}catch(Exception e) {
//			transactionManager.rollback(status); //트랜잭션 롤백
//			throw e;
//		}
	}
	
	private boolean canUpgradeLevel(User user) {
		Level currentLevel = user.getLevel();
		switch(currentLevel) { //레벨별로 구분해서 조건을 판단한다.
		case BASIC :
			return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
		case SILVER : 
			return (user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD);
		case GOLD : 
			return false;
		default : 
			//현재 로직에서 다룰 수 없는 레벨이 주어지면 예외를 발생시킨다.
			//새로운 레벨이 추가되고 로직을 수정하디 않으면 에러가 나서 확인할 수 있다.
			throw new IllegalArgumentException("Unknown Level: " + currentLevel); 
		}
	}
	
	/*
	 * upgradeLevel() 메서드 코드는 별로 좋지 못하다.
	 *  - 다음 단계가 무엇인가 하는 로직과 그때 사용자 오브젝트의 level필드를 변경해준다는 로직이 함께 있는데다, 너무 노골적으로 드러나 있다
	 *  - 예외 상황에 대한 처리가 없다.(만약 업그레이드 조건을 잘못 파악해서 더 이상 다음 단계가 없는 GOLD레벨인 사용자를 업그레이드 한다면?)
	 *  - 레벨이 늘어나면 if문이 점점 길어질 것이고, 레벨 변경 시 사용자 오브젝트에서 level필드 외의 값도 같이 변경해야 한다면 if조건 뒤에 붙는 내용도 점점 길어질 것이다.
	 */
//	private void upgradeLevel(User user) {
//		if(user.getLevel() == Level.BASIC) { //먼저 레벨의 순서와 다음 단계 레벨이 무엇인지를 결정하는 일은 Level에게 맡기자.
//			user.setLevel(Level.SILVER);
//		}else if(user.getLevel() == Level.SILVER) {
//			user.setLevel(Level.GOLD);
//		}
//		userDao.update(user);
//	}
	
	/*
	 * 위의 upgradeLevel()과는 다르게 코드가 간결해지고 작업 내용이 명확해졌다.
	 * 이유로는 각 오브젝트와 메서드가 각각 자기 몫의 책임을 맡아 일을 하는 구조이다. UserService,User,Level이 내부 정보를 다루는 자신의 책임에 충실한 기능을 갖고 있으면서
	 * 필요가 생기면 이런 작업을 수행해달라고 서로 요청하는 구조인것이다.
	 *  - 객체지향적인 코드는 다른 오브젝트의 데이터를 가져와서 작업하는 대신 데이터를 갖고 있는 다른 오브젝트에게 작업을 해달라고 요청한다.
	 *    오브젝트에게 데이터를 요구하지 말고 작업을 요청하라는 것이 객체지향 프로그래밍의 가장 기본이 되는 원리이기도 하다.
	 * 
	 */
	protected void upgradeLevel(User user) {
		user.upgradeLevel();
		userDao.update(user);
		sendUpgradeEMail(user);
	}
	
	public void add(User user) {
		if(user.getLevel() == null) 
			user.setLevel(Level.BASIC);
		userDao.add(user);
	}
	
//	private void sendUpgradeEMail(User user) {
//		Properties props = new Properties();
//		props.put("mail.smtp.host", "mail.ksug.org");
//		Session s = Session.getInstance(props, null);
//		
//		MimeMessage message = new MimeMessage(s);
//		try {
//			message.setFrom(new InternetAddress("useradmin@ksug.org"));
//			message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
//			message.setSubject("Upgrade 안내");
//			message.setText("사용자님의 등급이 " + user.getLevel().name() + "로 업그레이드되었습니다");
//			Transport.send(message);
//		}catch(AddressException e) {
//			throw new RuntimeException(e);
//		}catch(MessagingException e) {
//			throw new RuntimeException(e);
//		}
//	}
	
	private void sendUpgradeEMail(User user) {
//		JavaMailSenderImpl mailSender = new JavaMailSenderImpl(); 
				//JavaMail API를 사용하는 JavaMailSenderImpl클래스의 오브젝트를 코드에서 직접 사용하고 있어서 Javamail API를 사용하지 않는 테스트용 오브젝트로
				//대체 불가능 그래서 DI를 사용해야함
//		mailSender.setHost("mail.server.com");
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(user.getEmail());
		mailMessage.setFrom("useradmin@ksug.org");
		mailMessage.setSubject("Upgrade 안내");
		mailMessage.setText("사용자님의 등급이 " + user.getLevel().name());
		
		mailSender.send(mailMessage);
	}
}	
























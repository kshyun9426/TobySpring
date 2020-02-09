package springbook.user.service;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import springbook.user.domain.User;

/*
 * 비즈니스 트랜잭션 처리를 담은 UserServiceTx
 * 같은 UserService인터페이스를 구현한 다른 오브젝트에게 작업을 위임하게 만든다. 적어도 비즈니스 로직에 대해서는 UserServiceTx가 아무런 관여도 하지 않는다.
 */
public class UserServiceTx implements UserService {

	UserService userService;
	PlatformTransactionManager transactionManager;
	
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	@Override
	public void add(User user) { 
		userService.add(user); //위임
	}

	@Override
	public void upgradeLevels() {
		TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
		try {
			userService.upgradeLevels();
			this.transactionManager.commit(status);
		}catch(RuntimeException e) {
			this.transactionManager.rollback(status);
			throw e;
		}
		
	}

	
	
}

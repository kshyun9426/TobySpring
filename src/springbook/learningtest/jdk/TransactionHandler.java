package springbook.learningtest.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

//다이내믹 프록시를 위한 트랜잭션 부가기능 
public class TransactionHandler implements InvocationHandler {

	private Object target; //부가기능을 제공할 타겟 오브젝트, 어떤 타입의 오브젝트에도 적용 가능하다.
	private PlatformTransactionManager transactionManager; //트랜잭션 기능을 제공하는 데 필요한 트랜잭션 매니저
	private String pattern; //트랜잭션을 적용할 메서드 이름 패턴
	
	public void setTarget(Object target) {
		this.target = target;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if(method.getName().startsWith(pattern)) { //트랜잭션 적용 대상 메서드를 선별해서 트랜잭션 경계설정 기능을 부여한다.
			return invokeInTransaction(method, args);
		}else {
			return method.invoke(target, args);
		}
	}
	
	private Object invokeInTransaction(Method method, Object[] args) throws Throwable {
		TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
		try {
			Object ret = method.invoke(target, args);
			this.transactionManager.commit(status);
			return ret;
		}catch(InvocationTargetException e) { 
			//리플랙션 메서드인 Method.invoke()를 이용해 타겟 오브젝트의 메서드를 호출할 때는 타겟 오브젝트에서 발생하는 예외가 InvocationTargetException으로
			//한 번 포장해서 전달된다. 따라서 일단 발생하는 InvocationTargetException으로 받은 후 getTargetException()메서드로 중첩되어 있는 예외를 가져와야한다.
			this.transactionManager.rollback(status);
			throw e.getTargetException();
		}
	}
	
}
























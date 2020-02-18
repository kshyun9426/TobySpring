package springbook.user.service;

import java.lang.reflect.Proxy;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import springbook.learningtest.jdk.TransactionHandler;

public class TxProxyFactoryBean implements FactoryBean<Object> {

	Object target;
	PlatformTransactionManager transactionManager;
	String pattern;
	Class<?> serviceInterface; //다이내믹 프록시를  생성할 때 필요하다. UserService외의 인터페이스를 가진 타겟에도 적용할 수 있다.
	
	public void setTarget(Object target) {
		this.target = target;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public void setServiceInterface(Class<?> serviceInterface) {
		this.serviceInterface = serviceInterface;
	}

	@Override
	public Object getObject() throws Exception {
		TransactionHandler txHandler = new TransactionHandler();
		txHandler.setTarget(target);
		txHandler.setTransactionManager(transactionManager);
		txHandler.setPattern(pattern);
		return Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {serviceInterface}, txHandler);
	}

	//팩토리빈이 생성하는 오브젝트의 타입은 DI받은 인터페이스 타입에 따라 달라진다. 따라서 다양한 타입의 프록시 오브젝트 생성에 재사용 할 수 있다.
	@Override
	public Class<?> getObjectType() {
		return serviceInterface; 
	}

	//싱글톤 빈이 아니라는 뜻이 아니라 getObject()가 매번 같은 오브젝트를 리턴하지 않는다는 의미이다.
	@Override
	public boolean isSingleton() {
		return false;
	}
	
	
}























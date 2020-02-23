package springbook.learningtest.jdk;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

public class DynamicProxyTest {
	
	//JDK 다이내믹 프록시 생성
	@Test
	public void simpleProxy() {
		Hello proxiedHello = (Hello)Proxy.newProxyInstance(
				getClass().getClassLoader(),
				new Class[] {Hello.class},
				new UppercaseHandler(new HelloTarget()));
	}
	
	@Test
	public void proxyFactoryBean() {
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget()); //타겟 설정
		pfBean.addAdvice(new UppercaseAdvice()); //부가기능을 담은 어드바이스를 추가한다. 여러 개를 추가할 수도 있다.
		
		Hello proxiedHello = (Hello)pfBean.getObject(); //FactoryBean이므로 getObject()로 생성된 프록시를 가져온다.
		
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
	}
	
	static class UppercaseAdvice implements MethodInterceptor {
		//리플랙션의 Method와 달리 메서드 실행 시 타겟 오브젝트를 전달할 필요가 없다. MethodInvation은 메서드 정보와 함께 타겟 오브젝트를 알고 있기 때문이다.
		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			String ret = (String)invocation.proceed(); //proceed()메서드를 호출하면 타겟 오브젝트의 메서드를 내부적으로 실행해주는 기능이 있다.
			return ret.toUpperCase();
		}
	}
	
	static interface Hello { //타겟과 프록시가 구현할 인터페이스
		String sayHello(String name);
		String sayHi(String name);
		String sayThankYou(String name);
	}
	
	static class HelloTarget implements Hello {
		@Override
		public String sayHello(String name) {
			return "Hello " + name;
		}

		@Override
		public String sayHi(String name) {
			return "Hi " + name;
		}

		@Override
		public String sayThankYou(String name) {
			return "Thank You " + name;
		}
	}
	
	
	@Test
	public void pointcutAdvisor() {
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget());
		
		//메서드 이름을 비교해서 대상을 선정하는 알고리즘을 제공하는 포인트컷 생성
		NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut(); 
		pointcut.setMappedName("sayH*"); //이름 비교조건 설정 sayH로 시작하는 모든 메서드를 선택하게 한다.
		
		pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice())); //포인트컷과 어드바이스를 Advisor로 묶어서 한 번에 추가
		
		Hello proxiedHello = (Hello)pfBean.getObject();
		
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		
		//메서드 이름이 포인트컷의 선정조건에 맞지 않으므로, 부가기능이 적용되지 않는다.
		assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby")); 
	}
	
	
	/*
	 * 확장 포인트컷 테스트
	 */
	@Test
	public void classNamePointcutAdvisor() {
		NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut() {
			@Override
			public ClassFilter getClassFilter() {
				return new ClassFilter() {
					@Override
					public boolean matches(Class<?> clazz) {
						return clazz.getSimpleName().startsWith("HelloT"); //클래스 이름이 HelloT로 시작하는 것만 선정
					}
				};
			}
		};
		classMethodPointcut.setMappedName("sayH*"); //sayH로 시작하는 메서드 이름을 가진 메서드만 선정한다.
		
		
		//테스트
		checkAdviced(new HelloTarget(), classMethodPointcut, true); //적용클래스
		
		class HelloWorld extends HelloTarget {}
		checkAdviced(new HelloWorld(), classMethodPointcut, false); //적용클래스가 아님
		
		class HelloToby extends HelloTarget {}
		checkAdviced(new HelloToby(), classMethodPointcut, true); //적용클래스
	}
	
	private void checkAdviced(Object target, Pointcut pointcut, boolean adviced) {
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(target);
		pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
		Hello proxiedHello = (Hello)pfBean.getObject();
		
		if(adviced) { //advice적용대상이라면
			assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY")); 		//메서드 선정 방식을 통해 어드바이스 적용됨
			assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));		 		//메서드 선정 방식을 통해 어드바이스 적용됨
			assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby")); //메서드 선정 방식에 의해 적용 안됨
		}else { //advice적용대상이 아니라면
			assertThat(proxiedHello.sayHello("Toby"), is("Hello Toby")); 		
			assertThat(proxiedHello.sayHi("Toby"), is("Hi Toby"));		 		
			assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby")); 
		}
	}
	
}















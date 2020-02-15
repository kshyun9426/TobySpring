package springbook.learningtest.jdk;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class HelloProxyTest {
	
	//Hello 인터페이스를 통해 HelloTarget오브젝트를 사용하는 클라이언트 역할을 하는 간단한 테스트
	@Test
	public void simpleProxy() {
		Hello hello = new HelloTarget(); //타겟은 인터페이스를 통해 접근하는 습관을 들이자
		
		assertThat(hello.sayHello("Toby"), is("Hello Toby"));
		assertThat(hello.sayHi("sh"), is("Hi sh"));
		assertThat(hello.sayThankyou("sh"), is("Thank you sh"));
	}
	
	@Test
	public void upperProxy() {
		Hello proxiedHello = new HelloUppercase(new HelloTarget()); //프록시를 통해 타깃 오브젝트에 접근하도록 구성한다.
		
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		assertThat(proxiedHello.sayThankyou("Toby"), is("THANK YOU TOBY"));
	}
}

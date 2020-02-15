package springbook.learningtest.jdk;

/*
 * 프록시에는 데코레이터 패턴을 적용해서 타깃인 HelloTarget에 부가기능을 추가
 * 추가할 기능은 리턴하는 문자를 모두 대문자로 바꿔주는 것이다.
 */
//이 프록시는 프록시 적용의 일반적인 문제점 두 가지를 모두 갖고 있다. 1.인터페이스의 모든 메서드를 구현해 위임하도록 코드를 만들어야 하며, 
//2.부가기능인 리턴값을 대문자로 바꾸는 기능이 모든 메서드에 중복돼서 나타난다.
public class HelloUppercase implements Hello {

	Hello hello; //위임할 타겟 오브젝트. 여기서는 타겟 클래스의 보으젝트인 것은 알지만 다른 프록시를 추가할 수도 있으므로 인터페이스로 접근한다.
	
	public HelloUppercase(Hello hello) {
		this.hello = hello;
	}

	@Override
	public String sayHello(String name) {
		return hello.sayHello(name).toUpperCase(); //위임과 부가기능 적용
	}

	@Override
	public String sayHi(String name) {
		return hello.sayHi(name).toUpperCase();
	}

	@Override
	public String sayThankyou(String name) {
		return hello.sayThankyou(name).toUpperCase();
	}

	
	
}

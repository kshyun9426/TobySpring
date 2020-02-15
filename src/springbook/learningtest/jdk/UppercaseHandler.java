package springbook.learningtest.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class UppercaseHandler implements InvocationHandler {

//	Hello target;
	
	Object target;
	
	//다이내믹 프록시로부터 전달받은 요청을 다시 타겟 오브젝트에 위임해야 하기 때문에 타겟 오브젝트를 주입받아 둔다.
	//어떤 종류의 인터페이스를 구현한 타겟에도 적용 가능하도록 Object타입으로 수정
	public UppercaseHandler(Object target) {
		this.target = target;
	}
	
	//다이내믹 프록시가 클라이언트로부터 받는 모든 요청은 invoke()메서드로 전달된다. 다이내믹 프록시를 통해 요청이 전달되면 리플렉션 API를 이용해 타겟 오브젝트의 메서드를 호출한다.
	//InvocationHandler는 단일 메서드에서 모든 요청을 처리하기 때문에 어떤 메서드에 어떤 기능을 적용할지를 선택하는 과정이 필요할 수도 있다. 호출하는 메서드의 이름, 
	//파라미저의 개수와 타입, 리턴 타입등의 정보를 가지고 부가적인 기능을 적용할 메서드를 선택할 수 있다.
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object ret = method.invoke(target, args); //타겟으로 위임
		if(ret instanceof String) {
			return ((String)ret).toUpperCase(); //부가기능 제공, 리턴된 값은 다이내믹 프록시가 받아서 최종적으로 클라이언트에게 전달된다.
		}else {
			return ret;
		}
		
	}
	
}

























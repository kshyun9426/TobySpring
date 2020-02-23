package springbook.learningtest.jdk;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.util.PatternMatchUtils;

/*
 * 클래스 필터가 포함된 포인트컷
 */
public class NameMatchClassMethodPointcut extends NameMatchMethodPointcut {
	
	
	public void setMappedClassName(String mappedClassName) {
		//모든 클래스를 다 허용하던 디폴트 클래스 필터를 프로퍼티로 받은 클래스 이름을 이용해서 필터를 만들어 덮어씌운다.
		this.setClassFilter(new SimpleClassFilter(mappedClassName));  
	}
	
	static class SimpleClassFilter implements ClassFilter {

		String mappedName;
		
		private SimpleClassFilter(String mappedName) {
			this.mappedName = mappedName;
		}
		
		@Override
		public boolean matches(Class<?> clazz) {
			//와일트카드(*)가 들어간 분자열 비교를 지원하는 스프링의 유틸리티 메서드다. *name, name*, *name* 세 가지 방식을 모두 지원한다.
			return PatternMatchUtils.simpleMatch(mappedName, clazz.getSimpleName());
		}
		
	}
	
}

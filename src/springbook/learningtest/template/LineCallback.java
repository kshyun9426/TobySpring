package springbook.learningtest.template;

//제네릭을 이용한 범용적인 콜백 인터페이스 생성
public interface LineCallback<T> {
	
//	Integer doSomethingWithLine(String line, Integer value);
	T doSomethingWithLine(String line, T value);
	
}

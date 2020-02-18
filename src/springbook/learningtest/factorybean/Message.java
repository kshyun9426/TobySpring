package springbook.learningtest.factorybean;

public class Message {
	
	String text;
	
	//Message클래스는 생성자를 통해 오브젝트를 생성할 수 없다.
	private Message(String text) {
		this.text = text;
	}
	
	public String getText() {
		return this.text;
	}
	
	//생성자 대신 사용할 수 있는 스태틱 팩토리 메서드를 제공한다.
	public static Message newMessage(String text) {
		return new Message(text);
	}
	
}

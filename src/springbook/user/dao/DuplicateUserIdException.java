package springbook.user.dao;


/*
 * 중첩예외 생성
 * 필요하면 언제든 잡아서 처리할 수 있도록 별도의 예외로 정의하기는 하지만, 필요 없다면 신경 쓰지 않아도 되도록 RuntimeException을 상속한 런타임 예외로 만든다.
 */
public class DuplicateUserIdException extends RuntimeException {
	public DuplicateUserIdException(Throwable cause) {
		super(cause);
	}
	
}

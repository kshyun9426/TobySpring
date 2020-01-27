package springbook.user.domain;

/*
 * Enum을 사용하는 이유
 *  1. 일정한 종류의 정보를 문자열로 넣는 것은 별로 좋치 못함 대신 코드화해서 숫자로 넣는것이 DB용량도 많이 차지하지 않으며 가벼워서 좋다.
 *  2. 특정 데이터를기본 자료형을 사용하게 되면 잘못된 정보를 넣는 실수가 생겨도 컴파일러는 알지 못하고, 예상한 값의 범위를 벗어나게 입력할 수 있는 위험이 존재하기 때문에 
 */
public enum Level {
	
	//이늄 선언에 DB에 저장할 값과 함께 다음 단계의 레벨 정보도 추가
	//레벨의 업그레이드 순서는 Level이늄 안에서 관리할 수 있다.
	GOLD(3,null), SILVER(2,GOLD), BASIC(1,SILVER);
	
	private final int value;
	private final Level next; 
	
	Level(int value, Level next){
		this.value = value;
		this.next = next;
	}
	
	public int intValue() {
		return value;
	}
	
	public Level nextLevel() {
		return this.next;
	}
	
	public static Level valueOf(int value) {
		switch(value) {
		case 1: 
			return BASIC;
		case 2:
			return SILVER;
		case 3:
			return GOLD;
		default:
			throw new AssertionError("Unknown value: " + value);
		}
	}
	
}

package springbook.user.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import springbook.user.domain.Level;
import springbook.user.domain.User;

/*
 * User클래스에 대한 테스트는 굳이 스프링의 테스트 컨텍스트를 사용하지 않아도 된다.
 * User오브젝트는 스프링이 IoC로 관리해주는 오브젝트가 아니기 때문이다.
 */
public class UserTest {
	
	
	User user;
	
	@Before
	public void setUp() {
		user = new User();
	}
	
	//upgradeLevel()테스트는 Level이늄에 정의된 모든 레벨을 가져와서 User에 설정해두고 User의 upgradeLevel()을 실행해서 다음 레벨로 바뀌는지를 확인하는 것이다.
	//이렇게 테스트를 만들어 두면 나중에 upgradeLevel()메서드에 좀 더 복잡한 기능이 추가됐을 때도 이 테스트를 확장해 사용할 수 있다.
	@Test
	public void upgradeLevel() {
		Level[] levels = Level.values();
		for(Level level : levels) {
			if(level.nextLevel() == null) continue;
			user.setLevel(level);
			user.upgradeLevel();
			assertThat(user.getLevel(), is(level.nextLevel()));
		}
	}
	
	//더 이상 업그레이드할 레벨이 없는 경우에 upgradeLevel()을 호출하면 예외가 발생하는지를 확인하는 테스트다.
	@Test(expected=IllegalStateException.class)
	public void cannotUpgradeLevel() {
		Level[] levels = Level.values();
		for(Level level : levels) {
			if(level.nextLevel() != null) continue;
			user.setLevel(level);
			user.upgradeLevel(); //nextLevel()이 null인 경우에 강제로 upgradeLevel()을 호출해 본다.
		}
	}
	
}























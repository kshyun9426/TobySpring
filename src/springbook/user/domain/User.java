package springbook.user.domain;

public class User {
	
	String id;
	String name;
	String password;
	
	Level level;
	int login;
	int recommend;
	
	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public int getLogin() {
		return login;
	}

	public void setLogin(int login) {
		this.login = login;
	}

	public int getRecommend() {
		return recommend;
	}

	public void setRecommend(int recommend) {
		this.recommend = recommend;
	}

	public User() {
		
	}
	
//	public User(String id, String name, String password) {
//		this.id = id;
//		this.name = name;
//		this.password = password;
//	}
	
	public User(String id, String name, String password, Level level, int login, int recommend) {
		super();
		this.id = id;
		this.name = name;
		this.password = password;
		this.level = level;
		this.login = login;
		this.recommend = recommend;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}	
	
	//User의 내부 정보가 변경되는것이기 때문에 User가 책임을 맡는것이 낫다.
	public void upgradeLevel() {
		Level nextLevel = this.level.nextLevel();
		if(nextLevel == null) { //UserService의 canUpgradeLevel()에서 업그레이드 가능 여부를 미리 판단해주기는 하지만, User오브젝트를 UserService만
								//사용하는 건 아니므로 스스로 예외상황에 대한 검증기능을 갖고 있는 편이 안전하다.
			throw new IllegalStateException(this.level + "은 업그레이드가 불가능합니다.");
		}else {
			this.level = nextLevel;
		}
	}
}





















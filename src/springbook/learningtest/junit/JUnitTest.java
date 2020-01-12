package springbook.learningtest.junit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.junit.matchers.JUnitMatchers.either;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/*
 * 학습 테스트 예제
 *  - JUnit은 테스트 메서드를 수행할때 마다 새로운 오브젝트를 만든다고 했는데, 정말 그런지에 대해 학습테스트를 만들어 봤다.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/springbook/learningtest/junit/junit.xml") 
public class JUnitTest {
	
	@Autowired
	ApplicationContext context;
//	static JUnitTest testObject;
	static ApplicationContext contextObject = null;
	static Set<JUnitTest> testObjects = new HashSet<JUnitTest>();
	
	@Test
	public void test1() {
//		assertThat(this, is(not(sameInstance(testObject)))); //not()은 뒤에 나오는 결과를 부정하는 매쳐다.
															 //sameInstance()는 실제로 같은 오브젝트인지를 비교한다.
//		testObject = this;
		
		assertThat(testObjects, not(hasItem(this)));
		testObjects.add(this);
		assertThat(contextObject==null || contextObject == this.context, is(true));
		contextObject = this.context;
	}

	@Test
	public void test2() {
//		assertThat(this, is(not(sameInstance(testObject))));
//		testObject = this;
		
		assertThat(testObjects, not(hasItem(this)));
		testObjects.add(this);
		assertTrue(contextObject == null || contextObject == this.context);
		contextObject = this.context;
	}
	
	@Test
	public void test3() {
//		assertThat(this, is(not(sameInstance(testObject))));
//		testObject = this;
		
		assertThat(testObjects, not(hasItem(this)));
		testObjects.add(this);
		assertThat(contextObject, either(is(nullValue())).or(is(this.context)));
		contextObject = this.context;
	}
}





















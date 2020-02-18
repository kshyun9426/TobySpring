package springbook.learningtest.factorybean;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class FactoryBeanTest {
	
	@Autowired
	ApplicationContext context;
	
	@Test
	public void getMessageFromFactoryBean() {
		Object message = context.getBean("message");
		assertThat(message, is(Message.class));
		assertThat(((Message)message).getText(), is("Factory Bean"));
	}
	
	//드물지만 팩토리 빈이 만들어주는 빈 오브젝트가 아니라 팩토리 빈 자체를 가져오고 싶을 경우도 있다. 이럴 때 스프링은 '&'을 빈이름 앞에 붙여주면 팩토리 빈 자체를 돌려준다.
	@Test
	public void getFactoryBean() {
		Object factory = context.getBean("&message");
		assertThat(factory, is(MessageFactoryBean.class));
	}
	
}


















package com.xjdl.framework.beans.factory.support;

import com.xjdl.app.service.HelloWorldService;
import com.xjdl.framework.beans.factory.config.BeanDefinition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DefaultListableBeanFactoryTest {
	@Test
	void testBeanFactory() {
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		BeanDefinition beanDefinition = new BeanDefinition();
		beanDefinition.setBeanClass(HelloWorldService.class);

		beanFactory.registerBeanDefinition("service", beanDefinition);

		// 第一次创建并且放入单例池
		beanFactory.getBean("service");
		// 第二次从单例池中获取
		HelloWorldService service = (HelloWorldService) beanFactory.getBean("service");

		Assertions.assertNotNull(service);
	}
}

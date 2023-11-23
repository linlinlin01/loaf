package com.xjdl.framework.beans.factory.support;

import com.xjdl.framework.beans.BeansException;
import com.xjdl.framework.beans.factory.BeanFactory;
import com.xjdl.framework.beans.factory.config.BeanDefinition;
import com.xjdl.framework.beans.factory.config.BeanPostProcessor;
import com.xjdl.framework.beans.factory.config.ConfigurableBeanFactory;
import com.xjdl.framework.util.ClassUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 面向框架本身
 */
public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements ConfigurableBeanFactory {
	private final List<BeanPostProcessor> beanPostProcessors = new CopyOnWriteArrayList<>();
	private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
	private BeanFactory parentBeanFactory;

	public AbstractBeanFactory() {
	}

	public AbstractBeanFactory(BeanFactory parentBeanFactory) {
		this.parentBeanFactory = parentBeanFactory;
	}

	@Override
	public Object getBean(String name) throws BeansException {
		Object bean = super.getSingleton(name);
		if (bean != null) {
			return bean;
		}
		BeanDefinition beanDefinition = getBeanDefinition(name);
		return creatBean(name, beanDefinition);
	}

	/**
	 * 从 AbstractAutowireCapableBeanFactory 创建 Bean
	 */
	protected abstract Object creatBean(String name, BeanDefinition beanDefinition);

	/**
	 * 从 DefaultListableBeanFactory 获取 BeanDefinition
	 */
	protected abstract BeanDefinition getBeanDefinition(String name);

	@Override
	public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
		synchronized (this.beanPostProcessors) {
			this.beanPostProcessors.remove(beanPostProcessor);
			this.beanPostProcessors.add(beanPostProcessor);
		}
	}

	public List<BeanPostProcessor> getBeanPostProcessors() {
		return beanPostProcessors;
	}

	public ClassLoader getBeanClassLoader() {
		return this.beanClassLoader;
	}

	@Override
	public void setBeanClassLoader(ClassLoader beanClassLoader) {
		this.beanClassLoader = (beanClassLoader != null ? beanClassLoader : ClassUtils.getDefaultClassLoader());
	}

	@Override
	public BeanFactory getParentBeanFactory() {
		return this.parentBeanFactory;
	}

	@Override
	public void setParentBeanFactory(BeanFactory parentBeanFactory) {
		if (this.parentBeanFactory != null && this.parentBeanFactory != parentBeanFactory) {
			throw new IllegalStateException("Already associated with parent BeanFactory: " + this.parentBeanFactory);
		}
		if (this == parentBeanFactory) {
			throw new IllegalStateException("Cannot set parent bean factory to self");
		}
		this.parentBeanFactory = parentBeanFactory;
	}
}

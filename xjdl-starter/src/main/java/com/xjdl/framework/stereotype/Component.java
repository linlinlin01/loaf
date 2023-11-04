package com.xjdl.framework.stereotype;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 类上有这个注解才有机会进入单例池
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Component {
	String value() default "";

	/**
	 * 目前懒加载组件无法实现
	 */
	boolean lazyInit() default false;
}

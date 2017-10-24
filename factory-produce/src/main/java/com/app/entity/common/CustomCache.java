package com.app.entity.common;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomCache {
	
	
	/**
	 * 自定义缓存组
	 * @return
	 */
	public abstract int gorup() default 0;
	
	/**
	 * 排列顺序
	 * @return
	 */
	public abstract int sort();
	
	
	public abstract boolean hashKey() default false;
}

/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-7-13
 */
package com.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.app.entity.common.CacheVo;

@SpringBootApplication
public class App {

	public static void main(String[] args) {
		ApplicationContext applicationContext =SpringApplication.run(App.class, args);
		CacheVo.setApplicationContext(applicationContext);
		
	}
}

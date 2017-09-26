package com.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource(locations = { "classpath:application_bean.xml","classpath:beans-lookup.xml" })

public class ApplicationBeanXML {

}

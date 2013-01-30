package com.linkedhole.platform.infra.config;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.DispatcherServlet;

import com.ncsoft.platform.infra.config.AppConfig;

public class WebAppIntializer implements WebApplicationInitializer {

	public void onStartup(ServletContext servletContext) throws ServletException {
		
		// root 스프링 어필리케이션 컨텍스트 생성
		/*
		<context-param>
        	<param-name>contextConfigLocation</param-name>
        	<param-value>classpath:/META-INF/spring/*.xml</param-value>
    	</context-param>
		<listener>
            <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
        </listener>
        */
		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		String profilesActive = rootContext.getEnvironment().getProperty("spring.profiles.active");
		if(profilesActive==null) rootContext.getEnvironment().setActiveProfiles("dev");
		rootContext.register(AppConfig.class);
		servletContext.addListener(new ContextLoaderListener(rootContext));
		
		// servletContext에 등록 servletContext
		/*
		<servlet>
	        <servlet-name>dispatcher</servlet-name>
	        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
	        <init-param>
	            <param-name>contextConfigLocation</param-name>
	            <param-value>/WEB-INF/spring/servlet-context.xml</param-value>
	        </init-param>
	        <load-on-startup>1</load-on-startup>
	    </servlet>
	
	    <servlet-mapping>
	        <servlet-name>dispatcher</servlet-name>
	        <url-pattern>/</url-pattern>
	    </servlet-mapping>
	    */
		// dispatcher servlet 생성 
		AnnotationConfigWebApplicationContext dispatcherContext = new AnnotationConfigWebApplicationContext();
		dispatcherContext.register(WebConfig.class);

		// servl등록etContext에 dispatcher servlet 등록
		ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", new DispatcherServlet(dispatcherContext));
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/");
		
		/*
		<filter>
	        <filter-name>encodingFilter</filter-name>
	        <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
	        <init-param>
	            <param-name>encoding</param-name>
	            <param-value>UTF-8</param-value>
	        </init-param>
	        <init-param>
	            <param-name>forceEncoding</param-name>
	            <param-value>true</param-value>
	        </init-param>
	    </filter>
	
	    <filter-mapping>
	        <filter-name>encodingFilter</filter-name>
	        <url-pattern>/*</url-pattern>
	    </filter-mapping>
	    
	    <filter>
	        <filter-name>hiddenHttpMethodFilter</filter-name>
	        <filter-class>org.springframework.web.filter.HiddenHttpMethodFilter</filter-class>
	    </filter>
	
	    <filter-mapping>
	        <filter-name>hiddenHttpMethodFilter</filter-name>
	        <url-pattern>/*</url-pattern>
	    </filter-mapping>
	    
	    <filter>
		<filter-name>etagFilter</filter-name>
		<filter-class>org.springframework.web.filter.ShallowEtagHeaderFilter</filter-class>
		</filter>
		<filter-mapping>
			<filter-name>etagFilter</filter-name>
			<servlet-name>/*</servlet-name>
		</filter-mapping>		
	    */
		CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
		encodingFilter.setEncoding("UTF-8");
		encodingFilter.setForceEncoding(true);
		
		ShallowEtagHeaderFilter shallowEtagHeaderFilter = new ShallowEtagHeaderFilter();
		
		HiddenHttpMethodFilter hiddenHttpMethodFilter = new HiddenHttpMethodFilter();

		servletContext.addFilter("encodingFilter", encodingFilter)
		              .addMappingForServletNames(EnumSet.allOf(DispatcherType.class), true, "dispatcher");

		servletContext.addFilter("hiddenHttpMethodFilter", hiddenHttpMethodFilter)
		              .addMappingForServletNames(EnumSet.allOf(DispatcherType.class), true, "dispatcher");
		
		servletContext.addFilter("etagFilter", shallowEtagHeaderFilter)
        			  .addMappingForServletNames(EnumSet.allOf(DispatcherType.class), true, "dispatcher");
		
	}

}

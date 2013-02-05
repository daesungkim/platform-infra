package com.linkedhole.platform.infra.config;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestTemplateErrorHandler;

@Import({ DataConfig.class, TaskConfig.class })
@ImportResource({"classpath:spring/AppConfig.xml"})
@EnableAspectJAutoProxy // <aop:aspectj-autoproxy>
@ComponentScan(basePackages = "com.linkedhole", excludeFilters = {@ComponentScan.Filter(Configuration.class), @ComponentScan.Filter(Controller.class)})
public class AppConfig{
	
	@Inject
	private Environment environment;
	
	@Bean(name="messageSource")
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:/messages/message");
		messageSource.setDefaultEncoding("UTF-8");
		if (environment.acceptsProfiles("dev")) {
			messageSource.setCacheSeconds(0);
		}
		return messageSource;
	}
	
	@Bean(name="messageSourceAccessor")
	public MessageSourceAccessor messageSourceAccessor() {
		return new MessageSourceAccessor(messageSource());
	}
	
	@Bean(name="httpClientFactory")
	public HttpComponentsClientHttpRequestFactory httpClientFactory() {
		HttpComponentsClientHttpRequestFactory httpClientFactory = new HttpComponentsClientHttpRequestFactory(); 
		httpClientFactory.setConnectTimeout(3000);
		httpClientFactory.setReadTimeout(5000);
		return httpClientFactory;
	}
	
	@Bean(name="restTemplate")
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate(httpClientFactory());
		restTemplate.setErrorHandler(new RestTemplateErrorHandler());
		List<HttpMessageConverter<?>> converters =new ArrayList<HttpMessageConverter<?>>();
		converters.add(new FormHttpMessageConverter());
		converters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
		converters.add(new MappingJacksonHttpMessageConverter());
		converters.add(new Jaxb2RootElementHttpMessageConverter());
		restTemplate.setMessageConverters(converters);
		return restTemplate;
	}
}
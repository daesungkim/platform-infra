package com.linkedhole.platform.infra.config;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionException;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonpView;
import org.thymeleaf.spring3.SpringTemplateEngine;
import org.thymeleaf.spring3.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

@Configuration
@EnableWebMvc // <mvc:*>
@ImportResource({"classpath:spring/WebConfig.xml"})
@ComponentScan(basePackages = "com.linkedhole", useDefaultFilters = false, includeFilters = @ComponentScan.Filter(Controller.class))
public class WebConfig extends WebMvcConfigurerAdapter {

	@Inject 
	private ResourceLoader resourceLoader;
	
	@Inject
	private Environment environment;
	
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/favicon.ico").addResourceLocations("/resources/favicon.ico");
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/"); 
	}
	
	@Bean
	public HandlerExceptionResolver simpleMappingExceptionResolver() {
		
		Properties mappings = new Properties();
		mappings.put(NoSuchRequestHandlingMethodException.class.getName(), "/error/pageNotFound");
		mappings.put(HttpRequestMethodNotSupportedException.class.getName(), "/error/pageNotFound");		
		mappings.put(DataAccessException.class.getName(), "/error/dataAccessFailure");
		mappings.put(TransactionException.class.getName(), "/error/dataAccessFailure");
		
		SimpleMappingExceptionResolver exceptionResolver = new SimpleMappingExceptionResolver();
		exceptionResolver.setExceptionMappings(mappings);
		exceptionResolver.setDefaultErrorView("/error/defaultError");
		exceptionResolver.setDefaultStatusCode(HttpStatus.BAD_REQUEST.value());
		exceptionResolver.setOrder(1);
		
		return exceptionResolver;
	}

	public SessionLocaleResolver sessionLocaleResolver(){
		Locale locale = new Locale("ko");
		SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
		sessionLocaleResolver.setDefaultLocale(locale);
		return sessionLocaleResolver;
	}
	
	public LocaleChangeInterceptor localeChangeInterceptor(){
		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName("lang");
		return localeChangeInterceptor;
	}
	
	public DefaultRequestToViewNameTranslator defaultRequestToViewNameTranslator(){
		return new DefaultRequestToViewNameTranslator();
	}
	
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(new FormHttpMessageConverter());
		converters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
		converters.add(new MappingJacksonHttpMessageConverter());
		converters.add(new Jaxb2RootElementHttpMessageConverter());
	}
	
	public Validator getValidator() {
		LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename(":classpath/messages/validation");
		if (environment.acceptsProfiles("dev")) {
			messageSource.setCacheSeconds(0);
		}
		factory.setValidationMessageSource(messageSource);
		return factory;
	}
	
	/**
	 * Supports FileUploads.
	 */
	@Bean
	public MultipartResolver multipartResolver() {
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		multipartResolver.setMaxUploadSize(500000);
		return multipartResolver;
	}
	
	@Bean(name="viewNameTranslator")
	public DefaultRequestToViewNameTranslator viewNameTranslator(){
		return new DefaultRequestToViewNameTranslator();
	}
	
	@Bean(name="templateResolver")
	public ServletContextTemplateResolver templateResolver(){
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver();
		templateResolver.setPrefix("/WEB-INF/views/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode("HTML5");
		templateResolver.setCacheable(false);
		return templateResolver;
	}
	
	@Bean(name="templateEngine")
	public SpringTemplateEngine templateEngine(){
		SpringTemplateEngine templateEngine= new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver());
		return templateEngine;
	}
	
	@Bean(name="thymeleafViewResolver")
	public ThymeleafViewResolver thymeleafViewResolver(){
		ThymeleafViewResolver thymeleafViewResolver = new ThymeleafViewResolver();
		thymeleafViewResolver.setTemplateEngine(templateEngine());
		thymeleafViewResolver.setCharacterEncoding("UTF-8");
		return thymeleafViewResolver;
	}
	
	@Bean
	public ContentNegotiatingViewResolver contentNegotiatingViewResolver(){
		ContentNegotiatingViewResolver negotiating = new ContentNegotiatingViewResolver();
		
		// mediaTypes property
		Map<String, String> mediaTypes = new HashMap<String,String>();
		mediaTypes.put("html", "text/html");
		mediaTypes.put("json", "application/json");
		mediaTypes.put("jsonp","application/javascript");
		mediaTypes.put("xml",  "application/xml");
		mediaTypes.put("atom", "application/atom+xml");
		mediaTypes.put("pdf",  "application/pdf");
		mediaTypes.put("xsl",  "application/vnd.ms-excel");
		negotiating.setMediaTypes(mediaTypes);
		
		// viewResolvers property
		List<ViewResolver> viewResolvers = new ArrayList<ViewResolver>();
		viewResolvers.add(new BeanNameViewResolver());
		viewResolvers.add(thymeleafViewResolver());
		
		InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
		internalResourceViewResolver.setPrefix("/WEB-INF/views/");
		internalResourceViewResolver.setSuffix(".jsp");
		
		viewResolvers.add(internalResourceViewResolver);
		
		negotiating.setViewResolvers(viewResolvers);
		
		// defaultViews property
		List<View> JsonView = new ArrayList<View>();
		MappingJacksonJsonView mappingJacksonJsonView = new MappingJacksonJsonView();
		mappingJacksonJsonView.setPrefixJson(false);
		mappingJacksonJsonView.setModelKey("result");
		
		JsonView.add(mappingJacksonJsonView);
		
		MappingJacksonJsonpView mappingJacksonJsonpView = new MappingJacksonJsonpView();
		mappingJacksonJsonpView.setPrefixJson(false);
		mappingJacksonJsonpView.setModelKey("result");
		
		JsonView.add(mappingJacksonJsonpView);
		
		negotiating.setDefaultViews(JsonView);
		
		negotiating.setOrder(2);
		
		return negotiating;
	}
}
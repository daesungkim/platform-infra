package com.linkedhole.platform.infra.config;

import java.util.Properties;

import javax.inject.Inject;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class DataConfig {
	
	@Inject
	private Environment environment;
	
	@Configuration
	@Profile("dev")
	@PropertySource({"classpath:properties/profile_dev.properties", "classpath:properties/environment_dev.properties"})
	static class envDevProp {
	}

	@Configuration
	@Profile("live")
	@PropertySource({"classpath:properties/profile_live.properties", "classpath:properties/environment_live.properties"})
	static class envLiveProp {
	}
	
	@Bean(name="dataSource", destroyMethod="close")
	public DataSource dataSource() {
		DataSource ds = new DataSource();
		ds.setDriverClassName(environment.getRequiredProperty("service.jdbc.driverClassName"));
		ds.setUrl(environment.getRequiredProperty("service.jdbc.url"));
		ds.setUsername(environment.getRequiredProperty("service.jdbc.username"));
		ds.setPassword(environment.getRequiredProperty("service.jdbc.password"));
		ds.setMaxActive(environment.getRequiredProperty("service.jdbc.maxActive", int.class));
		ds.setMaxWait(environment.getRequiredProperty("service.jdbc.maxWait", int.class));
		ds.setMinIdle(environment.getRequiredProperty("service.jdbc.minIdle", int.class));
		ds.setInitialSize(environment.getRequiredProperty("service.jdbc.initialSize", int.class));
		ds.setValidationQuery(environment.getRequiredProperty("service.jdbc.validationQuery"));
		ds.setValidationInterval(environment.getRequiredProperty("service.jdbc.validationInterval", long.class));
		ds.setTestOnBorrow(environment.getRequiredProperty("service.jdbc.testOnBorrow", boolean.class));
		ds.setTestWhileIdle(environment.getRequiredProperty("service.jdbc.testWhileIdle", boolean.class));
		ds.setTimeBetweenEvictionRunsMillis(environment.getRequiredProperty("service.jdbc.timeBetweenEvictionRunsMillis", int.class));
		ds.setRemoveAbandoned(environment.getRequiredProperty("service.jdbc.removeAbandoned", boolean.class));
		ds.setRemoveAbandonedTimeout(environment.getRequiredProperty("service.jdbc.removeAbandonedTimeout", int.class));
		ds.setLogAbandoned(environment.getRequiredProperty("service.jdbc.logAbandoned", boolean.class));
		ds.setAbandonWhenPercentageFull(environment.getRequiredProperty("service.jdbc.abandonWhenPercentageFull", int.class));
		ds.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ResetAbandonedTimer;org.apache.tomcat.jdbc.pool.interceptor.ConnectionState");
		ds.setConnectionProperties("autoReconnect=true;roundRobinLoadBalance=true;characterEncoding=UTF-8;autoReconnectForPools=true;elideSetAutoCommits=true;logger=Slf4JLogger");
		return ds;
    }	
	
	@Bean
	public SessionFactory sessionFactory(){
		LocalSessionFactoryBuilder localSessionFactoryBuilder = new LocalSessionFactoryBuilder(dataSource());
		localSessionFactoryBuilder.scanPackages(new String[ ]{environment.getRequiredProperty("service.hibernate.scanpackages")});
		localSessionFactoryBuilder.addProperties(hibernateProperties());
		return localSessionFactoryBuilder.buildSessionFactory();
    }
	
	public Properties hibernateProperties(){
		Properties properties = new Properties();
		properties.setProperty("hibernate.dialect", environment.getRequiredProperty("service.hibernate.dialect"));
		properties.setProperty("hibernate.show_sql", environment.getRequiredProperty("service.hibernate.show_sql"));
		properties.setProperty("format_sql", environment.getRequiredProperty("service.hibernate.format_sql"));
		properties.setProperty("hibernate.hbm2ddl.auto", environment.getRequiredProperty("service.hibernate.hbm2ddl.auto"));
		properties.setProperty("hibernate.connection.release_mode", environment.getRequiredProperty("service.hibernate.connection.release_mode"));
		properties.setProperty("hibernate.auto_close_session", environment.getRequiredProperty("service.hibernate.auto_close_session"));
		return properties;
	}
	
    @Bean
    public PlatformTransactionManager transactionManager(){
      return new HibernateTransactionManager(sessionFactory());
    }
}

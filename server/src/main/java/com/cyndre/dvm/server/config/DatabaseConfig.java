package com.cyndre.dvm.server.config;

import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.cyndre.dvm.server.data.mapper")
public class DatabaseConfig {
	private static final int INITIAL_POOL_SIZE = 8;
	private static final int MAX_ACTIVE_POOL_SIZE = INITIAL_POOL_SIZE;
	private static final int MAX_IDLE_POOL_SIZE = 1;
	private static final long MAX_POOL_WAIT = TimeUnit.SECONDS.toMillis(5);
	
	@Value("${jdbc.driver}")
	private String jdbcDriver;
	
	@Value("${jdbc.url}")
	private String jdbcUrl;
	
	@Value("${jdbc.username}")
	private String jdbcUsername;
	
	@Value("${jdbc.password}")
	private String jdbcPassword;
	
	@Bean()
	protected SqlSessionFactory createSqlSessionFactory() {
		final SqlSessionFactoryBean sessionFac = new SqlSessionFactoryBean();
		
		sessionFac.setDataSource(createDataSource());
		
		try {
			return sessionFac.getObject();
		} catch (Exception e) {
			throw new ConfigurationException(e);
		}
	}
	
	@Bean(destroyMethod="close")
	protected DataSource createDataSource() {
		final BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(jdbcDriver);
		ds.setUrl(jdbcUrl);
		ds.setUsername(jdbcUsername);
		ds.setPassword(jdbcPassword);
		
		ds.setInitialSize(INITIAL_POOL_SIZE);
		ds.setMaxActive(MAX_ACTIVE_POOL_SIZE);
		ds.setMaxIdle(MAX_IDLE_POOL_SIZE);
		ds.setMaxWait(MAX_POOL_WAIT);
		
		return ds;
	}
	
	
}

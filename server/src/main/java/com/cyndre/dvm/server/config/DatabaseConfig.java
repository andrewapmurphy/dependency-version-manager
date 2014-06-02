package com.cyndre.dvm.server.config;

import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cyndre.dvm.server.data.project.ProjectMapper;

@Configuration
public class DatabaseConfig {
	private static final int INITIAL_POOL_SIZE = 8;
	private static final int MAX_ACTIVE_POOL_SIZE = INITIAL_POOL_SIZE;
	private static final int MAX_IDLE_POOL_SIZE = 1;
	private static final long MAX_POOL_WAIT = TimeUnit.SECONDS.toMillis(5);
	
	@Value("jdbc.driver")
	private String jdbcDriver;
	
	@Value("jdbc.url")
	private String jdbcUrl;
	
	@Bean(name="projectMapper")
	public ProjectMapper createProjectMapper() {
		try {
			return createMapperFactory(ProjectMapper.class).getObject();
		} catch (Exception e) {
			throw new ConfigurationException(e);
		}
	}
	
	private <T> MapperFactoryBean<T> createMapperFactory(final Class<T> mapperInterface) {
		final MapperFactoryBean<T> mapperFactory = new MapperFactoryBean<T>();
		//TODO: not sure if this is correct!!!!
		final SqlSessionFactory sqlFactory = createSessionFactoryBean();
	
		sqlFactory.getConfiguration().addMapper(mapperInterface);
	
		mapperFactory.setMapperInterface(mapperInterface);
		mapperFactory.setSqlSessionFactory(
			sqlFactory
		);
		
		return mapperFactory;
	}
	
	@Bean()
	protected SqlSessionFactory createSessionFactoryBean() {
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
		
		ds.setInitialSize(INITIAL_POOL_SIZE);
		ds.setMaxActive(MAX_ACTIVE_POOL_SIZE);
		ds.setMaxIdle(MAX_IDLE_POOL_SIZE);
		ds.setMaxWait(MAX_POOL_WAIT);
		
		return ds;
	}
	
	
}

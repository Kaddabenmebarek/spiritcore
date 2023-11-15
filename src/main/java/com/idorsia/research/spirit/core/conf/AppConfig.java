package com.idorsia.research.spirit.core.conf;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.dozer.DozerBeanMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.idorsia.research.spirit.core.util.DataUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;



@Configuration
@ComponentScan("com.idorsia.research.spirit.core")
@EnableTransactionManagement
public class AppConfig {

	@Bean
	public DataSource getDataSource() {
		
		Properties props = DataUtils.fetchProperties();
		String dbEnv = getDbEnvironment();		
		System.out.println("ENVIRO : " + dbEnv);
		HikariConfig config = new HikariConfig();
		
	    config.setDriverClassName(props.getProperty("db.driver"));
	    config.setJdbcUrl(props.getProperty(dbEnv+".db.url"));
	    config.setUsername(props.getProperty(dbEnv+".db.username"));
	    config.setPassword(props.getProperty(dbEnv+".db.password"));
	    
	    //config.setPoolName(/*props.getProperty("poolName")*/);
	    //maximumPoolSize: This value will determine the maximum number of actual connections to the databasebackend
	    config.setMaximumPoolSize(Integer.valueOf(props.getProperty("maximumPoolSize")));
	    
	    //minimumIdle: The minimum number of idle connections to maintain in the pool. 
	    config.setMinimumIdle(Integer.valueOf(props.getProperty("minimumIdle")));
	    
	    //connectionTimeout: Set the maximum number of milliseconds that a client will wait for a connection from the pool
	    config.setConnectionTimeout(Long.valueOf(props.getProperty("connectionTimeout")));
	    
	    //maximum idle time for connection
	    //config.setIdleTimeout(Long.valueOf(props.getProperty("idleTimeout")));
	    
	    //config.setKeepaliveTime(Long.valueOf(props.getProperty("keepaliveTime")));
	    
	    //config.addDataSourceProperty("implicitCachingEnabled", props.getProperty("implicitCachingEnabled"));
	    
	    //config.addDataSourceProperty("dataSource.logSlowQueries", "true");
	    //config.addDataSourceProperty("cacheServerConfiguration","true");
	    
	    HikariDataSource dataSource = new HikariDataSource(config);
		
		return dataSource;
	}

	@Bean(name = "spiritJdbcTemplate")
	public JdbcTemplate getJdbcTemplate() {
		TransactionManager tm = transactionManager();
		DataSource ds = ((DataSourceTransactionManager) tm).getDataSource();
		return new JdbcTemplate(ds);		
	}

	@Bean
	public TransactionManager transactionManager() {
		return new DataSourceTransactionManager(getDataSource());
	}

	@Bean(name = "org.dozer.Mapper")
	public DozerBeanMapper dozerBean() {
		List<String> mappingFiles = Arrays.asList("custom-converter-mapping.xml");
		DozerBeanMapper dozerBean = new DozerBeanMapper();
		dozerBean.setMappingFiles(mappingFiles);
		return dozerBean;
	}
	
	public String getDbEnvironment() {
		if("true".equalsIgnoreCase(System.getProperty("production"))) {
			return "prod";
		}
		if("true".equalsIgnoreCase(System.getProperty("development2"))) {			
			return "dev";
		}
		if("true".equalsIgnoreCase(System.getProperty("test"))) {
			return "test";
		}
		return null;
	}
	
//	@Bean(name="taskExecutor")
//	public Executor taskExecutor() {
//		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//		executor.setQueueCapacity(600);
//		executor.setMaxPoolSize(50);
//		executor.setCorePoolSize(50);
//		executor.setThreadNamePrefix("spiritPoolThread-");
//		executor.initialize();
//		return executor;
//	} 

}
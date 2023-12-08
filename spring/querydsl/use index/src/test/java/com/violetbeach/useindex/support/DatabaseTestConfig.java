package com.violetbeach.useindex.support;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.mysql.MySQLQueryFactory;
import java.sql.Connection;
import java.sql.SQLException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class DatabaseTestConfig {

	@PersistenceContext
	private EntityManager entityManager;

	@Bean
	public JPAQueryFactory jpaQueryFactory() {
		return new JPAQueryFactory(entityManager);
	}

	@Bean
	public MySQLQueryFactory mySQLQueryFactory(DataSource dataSource) throws SQLException {
		Connection connection = dataSource.getConnection();
		return new MySQLQueryFactory(() -> connection);
	}
}
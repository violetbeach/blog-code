package com.violetbeach.aop.repository;

import com.violetbeach.aop.support.DatabaseTestConfig;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(DatabaseTestConfig.class)
@EnableTransactionManagement(proxyTargetClass = true)
public abstract class RepositoryTestSupport {
}



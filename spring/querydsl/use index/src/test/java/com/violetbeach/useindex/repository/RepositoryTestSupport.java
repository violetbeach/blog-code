package com.violetbeach.useindex.repository;

import com.violetbeach.useindex.support.DatabaseTestConfig;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(DatabaseTestConfig.class)
public abstract class RepositoryTestSupport {
}



package com.violetbeach.sharding.repository;

import com.violetbeach.sharding.module.database.DBContextHolder;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class RepositoryTestSupport {

    @BeforeEach
    void setup() {
        DBContextHolder.setDbInfo("127.0.0.1", "01");
    }
}



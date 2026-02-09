package com.junwoo.hamkke.container;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 9.
 */
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class IntegrationTest {

    @Container
    static MySQLContainer<?> mysqlContainer =
            new MySQLContainer<>("mysql:8.0.32")
                    .withDatabaseName("test")
                    .withReuse(true);
}

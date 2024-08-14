package com.example.demo.infrastructure.kafka;

import javax.sql.DataSource;
import org.springframework.stereotype.Component;
import com.example.demo.infrastructure.kafka.impl.PostgreSQLDatabaseServiceImpl;

@Component
public class DatabaseServiceFactory {

    private final DataSource dataSource;
    private final PostgreSQLDatabaseServiceImpl postgreSQLDatabaseService;

    public DatabaseServiceFactory(DataSource dataSource, PostgreSQLDatabaseServiceImpl postgreSQLDatabaseService) {
        this.dataSource = dataSource;
        this.postgreSQLDatabaseService = postgreSQLDatabaseService;
    }

    public DatabaseService getDatabaseService() throws Exception {
        String databaseProductName = dataSource.getConnection().getMetaData().getDatabaseProductName();
        
        if (databaseProductName.equalsIgnoreCase("PostgreSQL")) {
            return postgreSQLDatabaseService;
        } else {
            throw new UnsupportedOperationException("Unsupported database: " + databaseProductName);
        }
    }
}

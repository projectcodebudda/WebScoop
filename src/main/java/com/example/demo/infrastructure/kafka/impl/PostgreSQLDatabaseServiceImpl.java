package com.example.demo.infrastructure.kafka.impl;

import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import com.example.demo.infrastructure.kafka.DatabaseService;

@Service
public class PostgreSQLDatabaseServiceImpl implements DatabaseService {

    private final JdbcTemplate jdbcTemplate;

    public PostgreSQLDatabaseServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void createTable(String tableName, Map<String, String> columns) {
        tableName = tableName.replace("-", "_"); // 하이픈을 언더스코어로 변경
        StringBuilder createTableQuery = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");
        columns.forEach((columnName, columnType) -> createTableQuery.append(columnName).append(" ").append(columnType).append(", "));
        createTableQuery.setLength(createTableQuery.length() - 2);
        createTableQuery.append(")");
        this.jdbcTemplate.execute(createTableQuery.toString());
    }

    @Override
    public void insertData(String tableName, Map<String, Object> data) {
        tableName = tableName.replace("-", "_"); // 하이픈을 언더스코어로 변경
        StringBuilder insertQuery = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
        StringBuilder valuesPart = new StringBuilder(" VALUES (");
        data.forEach((columnName, value) -> {
            insertQuery.append(columnName).append(", ");
            valuesPart.append("'").append(value.toString().replace("'", "''")).append("', "); 
        });
        insertQuery.setLength(insertQuery.length() - 2);
        valuesPart.setLength(valuesPart.length() - 2);
        insertQuery.append(")").append(valuesPart).append(")");
        this.jdbcTemplate.update(insertQuery.toString());
    }

    @Override
    public void insertInvalidData(int tableId, String data) {
        String insertInvalidDataQuery = "INSERT INTO invalid_data (tableid, data) VALUES (?, ?)";
        this.jdbcTemplate.update(insertInvalidDataQuery, tableId, data);
    }
}

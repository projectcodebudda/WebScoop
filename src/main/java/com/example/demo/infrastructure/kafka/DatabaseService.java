package com.example.demo.infrastructure.kafka;

import java.util.Map;

public interface DatabaseService {
    void createTable(String tableName, Map<String, String> columns);
    void insertData(String tableName, Map<String, Object> data);
    void insertInvalidData(int tableId, String data);
}

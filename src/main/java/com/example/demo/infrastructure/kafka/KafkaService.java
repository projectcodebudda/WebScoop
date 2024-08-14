package com.example.demo.infrastructure.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JdbcTemplate jdbcTemplate;

    public KafkaService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void processMessage(String tag, String dataType, String json, String userId) {
        try {
            if ("json".equals(dataType)) {
                processJsonData(tag, json, userId);
            } else if ("array".equals(dataType)) {
                processArrayData(tag, json, userId);
            } else {
                throw new IllegalArgumentException("Unsupported data type: " + dataType);
            }
        } catch (Exception e) {
            System.err.println("Error processing message with tag: " + tag + " and dataType: " + dataType);
            e.printStackTrace();
        }
    }

    private void processJsonData(String tag, String json, String userId) throws Exception {
        JsonNode jsonNode;
        try {
            jsonNode = this.objectMapper.readTree(json);
        } catch (Exception e) {
            insertInvalidData(json);
            return;
        }

        if (!jsonNode.isObject()) {
            insertInvalidData(json);
            return;
        }

        String tableName = getTableName("json");
        StringBuilder createTableQuery = new StringBuilder("CREATE TABLE ").append(tableName).append(" (");

        jsonNode.fieldNames().forEachRemaining(fieldName -> createTableQuery.append(fieldName).append(" TEXT, "));
        createTableQuery.setLength(createTableQuery.length() - 2); // Remove trailing comma
        createTableQuery.append(")");

        // Create the table
        jdbcTemplate.execute(createTableQuery.toString());

        // Insert data
        StringBuilder insertQuery = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
        StringBuilder valuesPart = new StringBuilder(" VALUES (");

        jsonNode.fieldNames().forEachRemaining(fieldName -> {
            insertQuery.append(fieldName).append(", ");
            valuesPart.append("'").append(jsonNode.path(fieldName).asText()).append("', ");
        });

        insertQuery.setLength(insertQuery.length() - 2); // Remove trailing comma
        valuesPart.setLength(valuesPart.length() - 2); // Remove trailing comma
        insertQuery.append(")").append(valuesPart).append(")");

        jdbcTemplate.update(insertQuery.toString());

        insertIntoTableList(tableName, userId);
    }

    private void processArrayData(String tag, String json, String userId) throws Exception {
        JsonNode jsonNode = objectMapper.readTree(json);

        if (!jsonNode.isArray()) {
            insertInvalidData(json);
            return;
        }

        JsonNode firstObject = jsonNode.get(0);
        if (!firstObject.isObject()) {
            insertInvalidData(json);
            return;
        }

        String tableName = getTableName("array");
        StringBuilder createTableQuery = new StringBuilder("CREATE TABLE ").append(tableName).append(" (");

        firstObject.fieldNames().forEachRemaining(fieldName -> createTableQuery.append(fieldName).append(" TEXT, "));
        createTableQuery.setLength(createTableQuery.length() - 2); // Remove trailing comma
        createTableQuery.append(")");

        jdbcTemplate.execute(createTableQuery.toString());

        for (JsonNode node : jsonNode) {
            StringBuilder insertQuery = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
            StringBuilder valuesPart = new StringBuilder(" VALUES (");

            firstObject.fieldNames().forEachRemaining(fieldName -> {
                insertQuery.append(fieldName).append(", ");
                valuesPart.append("'").append(node.path(fieldName).asText()).append("', ");
            });

            insertQuery.setLength(insertQuery.length() - 2); // Remove trailing comma
            valuesPart.setLength(valuesPart.length() - 2); // Remove trailing comma
            insertQuery.append(")").append(valuesPart).append(")");

            jdbcTemplate.update(insertQuery.toString());
        }

        insertIntoTableList(tableName, userId);
    }

    private void insertInvalidData(String json) {
        Integer latestId = getLastTableId();

        if (latestId == null) {
            System.err.println("No entry found in table_list");
            return;
        }

        String insertInvalidDataQuery = "INSERT INTO invalid_data (tableid, data) VALUES (?, ?)";
        jdbcTemplate.update(insertInvalidDataQuery, latestId, json);
    }

    private String getTableName(String dataType) {
        Integer latestId = getLastTableId();
        if (latestId == null) {
            latestId = 0; // 기본값으로 0을 사용
        }
        return String.format("table_%s_%d", dataType, latestId + 1); // ID를 +1
    }

    private Integer getLastTableId() {
        try {
            String query = "SELECT id FROM table_list ORDER BY id DESC LIMIT 1";
            return jdbcTemplate.queryForObject(query, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return null; // No entries found
        }
    }

    private void insertIntoTableList(String tableName, String userId) {
        try {
            jdbcTemplate.update("INSERT INTO table_list (tablename, userid) VALUES (?, ?)", tableName, userId);
        } catch (DuplicateKeyException e) {
            System.err.println("Duplicate entry found for table_list with table name: " + tableName);
        }
    }
}

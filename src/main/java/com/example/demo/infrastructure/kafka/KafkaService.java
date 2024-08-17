package com.example.demo.infrastructure.kafka;

import java.sql.PreparedStatement;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KafkaService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JdbcTemplate jdbcTemplate;

    public KafkaService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private enum JsonType {
        ARRAY,
        OBJECT,
        INVALID
    }

    private JsonType determineJsonType(JsonNode jsonNode) {
        if (jsonNode.isArray()) {
            return JsonType.ARRAY;
        } else if (jsonNode.isObject()) {
            return JsonType.OBJECT;
        } else {
            return JsonType.INVALID;
        }
    }

    public void processMessage(String json) {
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            JsonType jsonType = determineJsonType(jsonNode);

            switch (jsonType) {
                case ARRAY:
                    processArrayData(jsonNode);
                    break;
                case OBJECT:
                    processJsonData(jsonNode);
                    break;
                case INVALID:
                default:
                    insertInvalidData(json);
                    break;
            }
        } catch (JsonProcessingException e) {
            System.err.println("JSON parsing error: " + e.getMessage());
            insertInvalidData(json);
        } catch (Exception e) {
            System.err.println("Error processing message");
            e.printStackTrace();
            insertInvalidData(json);
        }
    }

    private void processJsonData(JsonNode jsonNode) throws Exception {
        String identifier = jsonNode.path("identifier").asText();
        if (identifier.isEmpty()) {
            insertInvalidData(jsonNode.toString());
            return;
        }

        String tableName = getTableName(identifier, "json");
        createTableIfNotExists(tableName, jsonNode);

        String insertQuery = createInsertQuery(tableName, jsonNode);
        jdbcTemplate.update(insertQuery);

        insertIntoTableList(tableName, null);
    }

    private void processArrayData(JsonNode jsonArray) throws Exception {
        JsonNode firstObject = jsonArray.get(0);
        String identifier = firstObject.path("identifier").asText();

        if (identifier.isEmpty()) {
            insertInvalidData(jsonArray.toString());
            return;
        }

        String tableName = getTableName(identifier, "array");
        createTableIfNotExists(tableName, firstObject);

        for (JsonNode node : jsonArray) {
            String insertQuery = createInsertQuery(tableName, node);
            jdbcTemplate.update(insertQuery);
        }

        insertIntoTableList(tableName, null);
    }

    private void createTableIfNotExists(String tableName, JsonNode jsonNode) {
        if (!tableExists(tableName)) {
            String createTableQuery = String.format("CREATE TABLE %s (", tableName);
            StringBuilder columns = new StringBuilder();
            jsonNode.fieldNames().forEachRemaining(fieldName -> columns.append(fieldName).append(" TEXT, "));
            createTableQuery += columns.substring(0, columns.length() - 2) + ")";
            jdbcTemplate.execute(createTableQuery);
        }
    }

    private String createInsertQuery(String tableName, JsonNode jsonNode) {
        StringBuilder insertQuery = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
        StringBuilder valuesPart = new StringBuilder(" VALUES (");

        jsonNode.fieldNames().forEachRemaining(fieldName -> {
            insertQuery.append(fieldName).append(", ");
            valuesPart.append("'").append(jsonNode.path(fieldName).asText().replace("'", "''")).append("', ");
        });

        insertQuery.setLength(insertQuery.length() - 2);
        valuesPart.setLength(valuesPart.length() - 2);
        insertQuery.append(")").append(valuesPart).append(")");

        return insertQuery.toString();
    }

    public void insertInvalidData(String json) {
        String invalidDataTableName = getTableName("invalid", "data");
        jdbcTemplate.update("INSERT INTO table_list (tablename) VALUES (?)", invalidDataTableName);

        Integer latestId = getLastTableId();

        if (latestId == null) {
            System.err.println("No entry found in table_list");
            return;
        }

        String insertInvalidDataQuery = "INSERT INTO invalid_data (tableid, data) VALUES (?, ?)";
        jdbcTemplate.update(insertInvalidDataQuery, latestId, json);
    }

    private String getTableName(String identifier, String dataType) {
        Integer latestId = getLastTableId();
        if (latestId == null) {
            latestId = 0;
        }

        String safeIdentifier = identifier.replace("-", "_");
        return String.format("%s_%s_%d", safeIdentifier, dataType, latestId + 1);
    }

    private Integer getLastTableId() {
        try {
            String query = "SELECT id FROM table_list ORDER BY id DESC LIMIT 1";
            return jdbcTemplate.queryForObject(query, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private void insertIntoTableList(String tableName, String userId) {
        syncSequenceWithLastId();  // 시퀀스와 테이블의 마지막 ID를 동기화

        try {
            jdbcTemplate.update("INSERT INTO table_list (tablename, userid) VALUES (?, ?)", tableName, userId);
        } catch (DuplicateKeyException e) {
            System.err.println("Duplicate entry found for table_list with table name: " + tableName);
        }
    }

    private void syncSequenceWithLastId() {
        Integer lastId = getLastTableId();
        if (lastId != null) {
            String query = "SELECT setval('table_list_id_seq', ?)";
            jdbcTemplate.execute(query, (PreparedStatement ps) -> {
                ps.setInt(1, lastId);
                ps.executeQuery();
                return null;  
            });
        }
    }

    private boolean tableExists(String tableName) {
        try {
            return jdbcTemplate.execute((StatementCallback<Boolean>) stmt -> {
                try (java.sql.ResultSet rs = stmt.executeQuery("SELECT 1 FROM " + tableName + " LIMIT 1")) {
                    return true;
                } catch (java.sql.SQLException e) {
                    return false;
                }
            });
        } catch (Exception e) {
            return false;
        }
    }
}

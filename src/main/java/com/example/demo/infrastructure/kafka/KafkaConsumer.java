package com.example.demo.infrastructure.kafka;

import java.util.concurrent.CountDownLatch;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KafkaConsumer {

    private final KafkaService kafkaService;
    private final ObjectMapper objectMapper;
    private CountDownLatch latch;

    public KafkaConsumer(KafkaService kafkaService) {
        this.kafkaService = kafkaService;
        this.objectMapper = new ObjectMapper(); 
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    @KafkaListener(topics = "my-topic", groupId = "test-group")
    public void onMessage(String message) {
        try {
            System.out.println("Received message: " + message);

            if (message.startsWith("{") || message.startsWith("[")) {
                JsonNode jsonNode = objectMapper.readTree(message);

                if (jsonNode.isObject() || jsonNode.isArray()) {
                    String json = jsonNode.toString();
                    kafkaService.processMessage(json);
                } else {
                    System.err.println("Received message is not in valid JSON format: " + message);
                    kafkaService.insertInvalidData(message);
                }
            } else {
                System.err.println("Received message is not in valid JSON format: " + message);
                kafkaService.insertInvalidData(message);
            }
        } catch (Exception e) {
        	kafkaService.insertInvalidData(message);
        } finally {
            if (latch != null) {
                latch.countDown();
            }
        }
    }
}
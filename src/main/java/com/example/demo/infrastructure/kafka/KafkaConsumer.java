package com.example.demo.infrastructure.kafka;

import java.util.concurrent.CountDownLatch;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {

    private final KafkaService kafkaService;
    private final ObjectMapper objectMapper;
    private CountDownLatch latch;
    
    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    @KafkaListener(topics = "my-topic", groupId = "test-group")
    public void onMessage(String message) {
        try {
            log.info("Received message: " + message);

            if (message.startsWith("{") || message.startsWith("[")) {
                JsonNode jsonNode = objectMapper.readTree(message);

                if (jsonNode.isObject() || jsonNode.isArray()) {
                    String json = jsonNode.toString();
                    this.kafkaService.processMessage(json);
                } else {
                    log.error("Received message is not in valid JSON format: " + message);
                    this.kafkaService.insertInvalidData(message);
                }
            } else {
                log.error("Received message is not in valid JSON format: " + message);
                this.kafkaService.insertInvalidData(message);
            }
        } catch (Exception e) {
        	this.kafkaService.insertInvalidData(message);
        } finally {
            if (this.latch != null) {
                this.latch.countDown();
            }
        }
    }
}
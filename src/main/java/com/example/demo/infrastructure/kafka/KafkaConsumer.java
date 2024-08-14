package com.example.demo.infrastructure.kafka;

import java.util.concurrent.CountDownLatch;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    private final KafkaService kafkaService;
    private CountDownLatch latch;

    public KafkaConsumer(KafkaService kafkaService) {
        this.kafkaService = kafkaService;
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    @KafkaListener(topics = "my-topic", groupId = "test-group")
    public void onMessage(String message) {
        try {
            System.out.println("Received message: " + message);

            // Extract tag, dataType, userId, and json from the message
            String[] parts = message.split("_", 4);

            if (parts.length == 4) {
                String tag = parts[0];
                String dataType = parts[1];
                String userId = parts[2];
                String json = parts[3];

                // Log for debugging
                System.out.println("Tag: " + tag);
                System.out.println("DataType: " + dataType);
                System.out.println("UserId: " + userId);
                System.out.println("JSON: " + json);

                // Process message
                kafkaService.processMessage(tag, dataType, json, userId);
            } else {
                System.err.println("Invalid message format: " + message);
            }
        } catch (Exception e) {
            // Handle exceptions or log the error
            System.err.println("Error processing Kafka message");
            e.printStackTrace();
        } finally {
            if (latch != null) {
                latch.countDown(); // Signal message reception
            }
        }
    }
}

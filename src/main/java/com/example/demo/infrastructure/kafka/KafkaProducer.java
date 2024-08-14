package com.example.demo.infrastructure.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String tag, String dataType, String message) {
        String fullMessage = tag + "_" + dataType + "\n" + message;
        kafkaTemplate.send("my-topic", fullMessage);
    }
}

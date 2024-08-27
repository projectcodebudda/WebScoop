package com.example.demo.infrastructure.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String tag, String dataType, String message) {
//        String fullMessage = tag + "_" + dataType + "\n" + message;
        this.kafkaTemplate.send("my-topic", message);
    }
}

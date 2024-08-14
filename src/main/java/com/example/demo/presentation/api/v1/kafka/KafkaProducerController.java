package com.example.demo.presentation.api.v1.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class KafkaProducerController {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerController(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String tag, String dataType, String json, String userId) {
        String message = String.format("%s_%s_%s_%s", tag, dataType, userId, json);
        kafkaTemplate.send("my-topic", message);
    }
}

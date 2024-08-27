package com.example.demo.presentation.api.v1.kafka;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.application.crawl.CrawlService;

@RestController
@RequestMapping("/api/v1/kafka")
public class KafkaProducerController {

    private final KafkaTemplate<String, String> kafkaTemplate;
    
    @Autowired
    private CrawlService crawlService;

    public KafkaProducerController(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/send")
    public void sendMessage(@RequestBody String json) {
        kafkaTemplate.send("my-topic", json);
    }

    @PostMapping("/url")
    public void crawlUrl(@RequestBody Map<String, String> request) {
        this.crawlService.getCrawlData(request);
    }
}

package com.example.demo.presentation.api.v1.kafka;

import java.util.Map;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.application.crawl.CrawlService;
import com.example.demo.infrastructure.kafka.KafkaProducer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/v1/kafka")
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerController {


    private final KafkaProducer producer;
    

    private final KafkaTemplate<String, String> kafkaTemplate;
    
    private final CrawlService crawlService;

    @PostMapping("/send")
    public void sendMessage(@RequestBody String json) {
        this.producer.sendMessage(null, null, json);
    }

    @PostMapping("/url")
    public void crawlUrl(@RequestBody Map<String, String> request) {
        this.crawlService.getCrawlData(request);
    }
}

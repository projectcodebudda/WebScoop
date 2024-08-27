package com.example.demo.infrastructure.kafka;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

import com.example.demo.presentation.api.v1.kafka.KafkaProducerController;

import jakarta.annotation.PostConstruct;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"my-topic"})
public class KafkaTest {

    @Autowired
    private KafkaProducerController kafkaProducerController;

    @Autowired
    private KafkaConsumer kafkaConsumer;

    private CountDownLatch latch;

    @PostConstruct
    public void setup() {
        this.latch = new CountDownLatch(1); 
        this.kafkaConsumer.setLatch(this.latch); 
    }

    @Test
    public void testProcessAndSaveValidJson() throws Exception {

        String validJson = "{\"url\":\"http://localhost:8080/crawl/examplePg\",\"identifier\":\"store-lego\",\"element\":\"N/A\",\"cLass\":\"25,000원\"}";
        this.kafkaProducerController.sendMessage(validJson);

        boolean received = this.latch.await(10, TimeUnit.SECONDS); 
        assertTrue(received, "Message was not received by the consumer");


    }

    @Test
    public void testProcessAndSaveValidArrayJson() throws Exception {

        String validArrayJson = "[{\"url\":\"http://localhost:8080/crawl/examplePg\",\"identifier\":\"store-lego\",\"element\":\"h2\",\"class\":\"레고 옥스포드 군인, 경찰, 등\"}," +
                                "{\"url\":\"http://localhost:8080/crawl/examplePg\",\"identifier\":\"store-lego\",\"element\":\"h2\",\"class\":\"레고 어벤져스 이터널스 배트맨슈퍼맨 등등 일괄\"}," +
                                "{\"url\":\"http://localhost:8080/crawl/examplePg\",\"identifier\":\"store-lego\",\"element\":\"h2\",\"class\":\"레고 닌자고 사무카이 팝니다\"}," +
                                "{\"url\":\"http://localhost:8080/crawl/examplePg\",\"identifier\":\"store-lego\",\"element\":\"h2\",\"class\":\"레고 10307 misb 판매한니다\"}]";
        this.kafkaProducerController.sendMessage(validArrayJson);

        boolean received = this.latch.await(10, TimeUnit.SECONDS);
        assertTrue(received, "Message was not received by the consumer");

    }

    @Test
    public void testProcessAndSaveInvalidJson() throws Exception {

        String invalidJson = "[{\"url\":\"http://localhost:8080/crawl/examplePg\",\"identifier\":\"store-lego\",\"element\":\"h2\",\"class\":\"레고 옥스포드 군인, 경찰, 등\"}," +
                              "{\"url\":\"http://localhost:8080/crawl/examplePg\",\"identifier\":\"store-lego\",\"element\":\"h2\",\"class\":\"레고 어벤져스 이터널스 배트맨슈퍼맨 등등 일괄\"}," +
                              "{\"url\":\"http://localhost:8080/crawl/examplePg\",\"identifier\":\"store-lego\",\"element\":\"h2\",\"class\":\"레고 닌자고 사무카이 팝니다\"}," +
                              "{\"url\":\"http://localhost:8080/crawl/examplePg\",\"identifier\":\"store-lego\",\"element\":\"h2\",\"class: \"레고 10307 misb 판매한니다\"}]"; // 잘못된 JSON 구문
        this.kafkaProducerController.sendMessage(invalidJson);

        boolean received = this.latch.await(10, TimeUnit.SECONDS);
        assertTrue(received, "Message was not received by the consumer");

    }
}

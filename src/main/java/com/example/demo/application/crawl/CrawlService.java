package com.example.demo.application.crawl;

import java.util.Map;

import org.springframework.http.ResponseEntity;

public interface CrawlService {
    ResponseEntity<Void> getCrawlData(Map<String, String> request);
}
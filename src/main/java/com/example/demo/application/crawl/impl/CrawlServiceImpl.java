package com.example.demo.application.crawl.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.application.crawl.CrawlService;
import com.example.demo.presentation.api.v1.kafka.KafkaProducerController;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CrawlServiceImpl implements CrawlService {

	private final Logger logger = LoggerFactory.getLogger(KafkaProducerController.class);


		@Override
		public ResponseEntity<Void> getCrawlData(Map<String, String> request) {
			try {
				String url = request.get("url");
				String keyword = request.get("word");
				String identifier = request.get("tpword");
				String element = request.getOrDefault("element", ""); // element가 없을 경우 빈 문자열
		
				// 크롤러 실행 및 결과를 로그로 출력
				String output = runCrawler("src/main/resources/file/crawl.exe", url, keyword, identifier, element);
				logger.info("Crawler Output:\n" + output);
		
				return ResponseEntity.ok().build(); // 성공적인 응답 반환
			} catch (Exception e) {
				logger.error(e.getMessage());
				return ResponseEntity.status(500).build(); // 오류 발생 시 500 상태 코드 반환
			}
		}

	    private String runCrawler(String... command) {
        StringBuilder output = new StringBuilder();
        try {
            // ProcessBuilder를 사용하여 명령어 실행
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
    
            // 표준 출력 스트림을 UTF-8로 읽기
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
    
            if (process.waitFor() != 0) {
                logger.error("크롤링 중 오류 발생");
            }
    
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return output.toString();
    }

}
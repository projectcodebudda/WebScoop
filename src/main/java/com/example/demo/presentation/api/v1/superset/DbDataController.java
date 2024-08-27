package com.example.demo.presentation.api.v1.superset;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.application.superset.DbDataService;
import com.example.demo.infrastructure.jpa.entity.TableListEntitiy;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/db")
@RequiredArgsConstructor
public class DbDataController {
	
	private final DbDataService dbService;
	
	@GetMapping("/table-list")
	public ResponseEntity<List<TableListEntitiy>> getTableList() {
		
		List<TableListEntitiy> data = this.dbService.getTableList();
		
		return new ResponseEntity<List<TableListEntitiy>>(data, HttpStatus.OK);
	}
}

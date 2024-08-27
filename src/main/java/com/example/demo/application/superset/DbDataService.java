package com.example.demo.application.superset;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.infrastructure.jpa.entity.TableListEntitiy;
import com.example.demo.infrastructure.jpa.repository.TableListRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DbDataService {
	
	private final TableListRepository tableListRepository;
	
	public List<TableListEntitiy> getTableList() {
		return Optional.of(this.tableListRepository.findAll()).orElse(null);
	}
}

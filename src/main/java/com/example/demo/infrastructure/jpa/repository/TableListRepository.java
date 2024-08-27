package com.example.demo.infrastructure.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.infrastructure.jpa.entity.TableListEntitiy;

public interface TableListRepository extends JpaRepository<TableListEntitiy, Long>{

}

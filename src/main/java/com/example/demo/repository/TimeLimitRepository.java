package com.example.demo.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.model.TimeLimit;

public interface TimeLimitRepository extends CrudRepository<TimeLimit, Long>{
	List<TimeLimit> findByProblemName(String problemName);
}

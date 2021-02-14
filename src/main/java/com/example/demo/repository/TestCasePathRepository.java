package com.example.demo.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.model.TestCasePath;

public interface TestCasePathRepository extends CrudRepository<TestCasePath, Long>{
	List<TestCasePath> findByProblemName(String problemName);
}

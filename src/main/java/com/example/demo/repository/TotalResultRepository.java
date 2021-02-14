package com.example.demo.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.model.TotalResult;

public interface TotalResultRepository extends CrudRepository<TotalResult, Long>{
	List<TotalResult> findBySubmissionId(long SubmissionId);
	List<TotalResult> findByUser(String userName);
}

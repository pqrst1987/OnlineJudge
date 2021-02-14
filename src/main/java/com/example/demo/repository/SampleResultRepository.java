package com.example.demo.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.model.SampleResult;

public interface SampleResultRepository extends CrudRepository<SampleResult, Long>{
	List<SampleResult> findBySubmissionNum(long submissionId);
	SampleResult findById(long sampleResultId);
}

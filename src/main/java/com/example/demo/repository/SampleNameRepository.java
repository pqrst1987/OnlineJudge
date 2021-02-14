package com.example.demo.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.model.SampleName;

public interface SampleNameRepository extends CrudRepository<SampleName, Long>{
	List<SampleName> findByTitle(String title);
	SampleName findById(long id);
}

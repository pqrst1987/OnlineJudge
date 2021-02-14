package com.example.demo.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.model.SpringCamp;

public interface SpringCampRepository extends CrudRepository<SpringCamp, Long>{
	List<SpringCamp> findByYearAndDayAndNumber(String year, int day, int number);
}

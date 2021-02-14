package com.example.demo.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class TestCasePath {
	@Id
	private Long id;
	private String problemName;
	private String testCasePath;
}

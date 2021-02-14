package com.example.demo.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class SampleName {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long sampleId;
	private String title;
	private String sampleName;
}

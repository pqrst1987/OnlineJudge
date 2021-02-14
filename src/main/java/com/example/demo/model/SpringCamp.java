package com.example.demo.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

@Entity
@Data
public class SpringCamp {
	@Id
	private Long problemId;

	@NotBlank
	@Size(max=20)
	private String year;

	@NotBlank
	private int day;

	@NotBlank
	private int number;

	@NotBlank
	@Size(max=100)
	private String title;
}

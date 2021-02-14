package com.example.demo.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import lombok.Data;

@Entity
@Data
@SequenceGenerator(name="EMP_SEQ", allocationSize = 1)
public class TotalResult {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long submissionId = 1L;

	private String user;
	private String problemName;
	private int totalResult; // ACを1, WAを2, TLEを3, REを4とする。

	public TotalResult() {}
	public TotalResult(Long id, String user, String problemName, int result){
		this.submissionId = id;
		this.user = user;
		this.problemName = problemName;
		this.totalResult = result;
	}
}

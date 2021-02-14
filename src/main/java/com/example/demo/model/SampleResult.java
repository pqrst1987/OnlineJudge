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
public class SampleResult {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long sampleResultId = 1L;


	private Long submissionNum;
	private String sampleName;
	private int sampleResult; // ACを1, WAを2, TLEを3, REを4とする。

	public SampleResult() {};
	public SampleResult(Long sampleResultId, Long submissionNum, String name, int result) {
		this.sampleResultId = sampleResultId;
		this.submissionNum = submissionNum;
		this.sampleName = name;
		this.sampleResult = result;
	}
}

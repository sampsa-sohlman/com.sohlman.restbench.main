package com.sohlman.restbench.model.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sohlman.restbench.model.Information;

public class InformationImpl implements Information{
	@JsonCreator
	public InformationImpl(@JsonProperty("word") String word, @JsonProperty("scientificValue") int scientificValue) {
		this.word = word;
		this.scientificValue = scientificValue;
	}

	public String getWord() {
		return word;
	}
	

	public int getScientificValue() {
		return scientificValue;
	}
	

	private String word;
	private int scientificValue;
}
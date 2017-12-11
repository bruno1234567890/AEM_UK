package com.qvc.selenium.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CucumberCase {

	private String keyword="Tasecase";
	private String name;
	private List<CucumberStep> steps = new ArrayList<>();
	
	
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<CucumberStep> getSteps() {
		return steps;
	}
	public void setSteps(List<CucumberStep> steps) {
		this.steps = steps;
	}
	
	@Override
	public String toString() {
		return "CucumberCase [keyword=" + keyword + ",  name=" + name + ", steps=" + steps + "]";
	}
	
	
	
}

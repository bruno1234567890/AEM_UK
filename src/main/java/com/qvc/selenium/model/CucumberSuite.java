package com.qvc.selenium.model;

import java.util.ArrayList;
import java.util.Collection;

public class CucumberSuite {

	private String keyword="Feature";
	private String name;
	private int passedCount;
	private int failedCount;
	private int totalCount;
	private Collection<CucumberCase> elements= new ArrayList<CucumberCase>();
	
	
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
	public int getPassedCount() {
		return passedCount;
	}
	public void setPassedCount(int passedCount) {
		this.passedCount = passedCount;
	}
	public int getFailedCount() {
		return failedCount;
	}
	public void setFailedCount(int failedCount) {
		this.failedCount = failedCount;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public Collection<CucumberCase> getElements() {
		return elements;
	}
	public void setElements(Collection<CucumberCase> elements) {
		this.elements = elements;
	}
	
	@Override
	public String toString() {
		return "CucumberSuite [keyword=" + keyword + ", name=" + name + ", passedCount=" + passedCount
				+ ", failedCount=" + failedCount + ", totalCount=" + totalCount + ", elements=" + elements + "]";
	}
	
	
	
	
}

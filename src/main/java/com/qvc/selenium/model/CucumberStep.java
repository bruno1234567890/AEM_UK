package com.qvc.selenium.model;


public class CucumberStep {
	private int line=1;
	private String keyword="Result";
	private String name;
	private CucumberResult result;
	public int getLine() {
		return line;
	}
	public void setLine(int line) {
		this.line = line;
	}
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
	public CucumberResult getResult() {
		return result;
	}
	public void setResult(CucumberResult result) {
		this.result = result;
	}
	
	@Override
	public String toString() {
		return "CucumberStep [line=" + line + ", keyword=" + keyword + ", name=" + name + ", result=" + result + "]";
	}

	
}

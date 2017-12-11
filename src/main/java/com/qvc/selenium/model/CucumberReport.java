package com.qvc.selenium.model;

import java.util.HashMap;
import java.util.Map;

public class CucumberReport {
	
	//for generate cucumber.json
	public static Map<String, CucumberSuite> suiteMap = new HashMap<String, CucumberSuite>();

	@Override
	public String toString() {
		return "CucumberReport [getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}


	
	

}

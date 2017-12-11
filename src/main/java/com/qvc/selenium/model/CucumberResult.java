package com.qvc.selenium.model;

public class CucumberResult {
	
	private long duration=0;
	private String status;
	
	
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return "CucumberResult [duration=" + duration + ", status=" + status + "]";
	}
	
	

}

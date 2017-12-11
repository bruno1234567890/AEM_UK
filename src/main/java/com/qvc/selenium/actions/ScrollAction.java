package com.qvc.selenium.actions;

import com.qvc.selenium.reporting.ExecuteResult;

public class ScrollAction extends BaseAction{

	@Override
	public ExecuteResult runAction() throws Exception {
		super.logAction();
		getWebElement();

		stepResult.setResult(true);
		stepResult.setStatus("Passed");
		stepResult.setStepDetail("Scroll Action to label: " + data.getStoredData("label"));
		return stepResult;

	}
}
